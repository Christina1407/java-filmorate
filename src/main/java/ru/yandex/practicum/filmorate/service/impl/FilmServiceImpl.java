package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmLikeStorage filmLikeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;


    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, UserService userService, FilmLikeStorage filmLikeStorage, FilmGenreStorage filmGenreStorage, GenreStorage genreStorage, RatingStorage ratingStorage, FilmDirectorStorage filmDirectorStorage, DirectorStorage directorStorage, FeedStorage feedStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
        this.filmLikeStorage = filmLikeStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.filmDirectorStorage = filmDirectorStorage;
        this.directorStorage = directorStorage;
        this.feedStorage = feedStorage;
    }

    //    ТЗ №10: каждый пользователь может поставить лайк фильму только один раз
    @Override
    public void addLike(Long filmId, Long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        if (!filmLikeStorage.usersIdsWhoLikeFilm(filmId).contains(userId)) { //добавляем лайк, если айдишника юзера нет в списке лайкнувших фильм,
            // ошибки при повторном добавлении не должно быть
            filmLikeStorage.addLike(userId, filmId);
            feedStorage.save(Feed.builder()
                    .userId(userId)
                    .entityId(filmId)
                    .eventType(EnumEventType.LIKE)
                    .operation(EnumOperation.ADD)
                    .timestamp(Instant.now().toEpochMilli())
                    .build());
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        if (filmLikeStorage.usersIdsWhoLikeFilm(filmId).contains(userId)) {//удаляем лайк, если айдишник юзера есть в списке лайкнувших фильм
            filmLikeStorage.deleteLike(userId, filmId);
            feedStorage.save(Feed.builder()
                    .userId(userId)
                    .entityId(filmId)
                    .eventType(EnumEventType.LIKE)
                    .operation(EnumOperation.REMOVE)
                    .timestamp(Instant.now().toEpochMilli())
                    .build());
        } else {
            log.error("Лайк юзера userId = " + userId + " фильму filmId = " + filmId + " не найден");
            throw new NotFoundException();
        }
    }

    @Override
    public List<Film> popularFilms(Integer count, Integer genreId, Integer year) {
        Comparator<Film> comparator = (film1, film2) -> {
            List<Long> whoLikeFilm1 = filmLikeStorage.usersIdsWhoLikeFilm(film1.getId());
            List<Long> whoLikeFilm2 = filmLikeStorage.usersIdsWhoLikeFilm(film2.getId());
            if (whoLikeFilm1.isEmpty() && whoLikeFilm2.isEmpty()) {
                return Math.toIntExact(film2.getId() - film1.getId());
            } else if (whoLikeFilm1.isEmpty()) {
                return 1;
            } else if (whoLikeFilm2.isEmpty()) {
                return -1;
            } else {
                return whoLikeFilm1.size() - whoLikeFilm2.size();
            }
        };
        List<Film> filmList = filmStorage.findAll();
        enrichFilmGenres(filmList);
        filmList = filmList.stream()
                .filter(film -> {
                    if (Objects.nonNull(year)) {
                        return Objects.equals(year, film.getReleaseDate().getYear());
                    } else {
                        return true;
                    }
                })
                .filter(film -> {
                    if (Objects.nonNull(genreId)) {
                        return film.getGenres().stream().anyMatch(genre -> Objects.equals(genre.getId(), genreId));
                    } else {
                        return true;
                    }
                })
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
        enrichFilmDirectors(filmList);
        return filmList;
    }

    @Override
    public Film saveFilm(Film film) {
        validateGenresAndMPA(film);
        validateDirectors(film);
        Film save = filmStorage.save(film);
        filmGenreStorage.insertIntoFilmGenres(save);
        filmDirectorStorage.insertIntoFilmDirectors(save);
        return findFilmById(save.getId());
    }

    @Override
    public void deleteFilm(Long filmId) {
        findFilmById(filmId);
        filmStorage.delete(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        validateGenresAndMPA(film);
        validateDirectors(film);
        Film update = filmStorage.update(film);
        if (Objects.isNull(update)) {
            log.error("Фильм не найден id {} ", film.getId());
            throw new NotFoundException();
        }
        //при обновлении нужно вносить изменения в БД film_genre,  film_director
        filmGenreStorage.updateFilmGenres(film);
        filmDirectorStorage.updateFilmDirectors(film);
        return findFilmById(update.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList = filmStorage.findAll();
        enrichFilmGenres(filmList);
        enrichFilmDirectors(filmList);
        return filmList;
    }

    @Override
    public Film findFilmById(Long filmId) {
        Film filmById = filmStorage.findFilmById(filmId);
        if (Objects.nonNull(filmById)) {
            enrichFilmGenres(List.of(filmById));
            enrichFilmDirectors(List.of(filmById));
            return filmById;
        } else {
            log.error("Не найден фильм id: {} ", filmId);
            throw new NotFoundException();
        }
    }

    @Override
    public List<Film> findFilmsByDirectorId(Long directorId, EnumSortBy sortBy) {
        if (Objects.isNull(directorStorage.findDirectorById(directorId))) {
            log.error("Не найден режиссёр id: {} ", directorId);
            throw new NotFoundException();
            //  throw new IncorrectParameterException("directorId " + directorId);
        }
        List<Film> films = filmStorage.findFilmsByDirector(directorId, sortBy);
        enrichFilmDirectors(films);
        enrichFilmGenres(films);
        return films;

    }

    @Override
    public List<Film> searchFilms(String query, List<String> searchByParams) {
        List<Film> films = filmStorage.searchFilms(query, searchByParams);
        enrichFilmGenres(films);
        enrichFilmDirectors(films);
        return films;
    }

    @Override
    public List<Film> findRecommendations(Long userId) {
        userService.findUserById(userId);
        List<FilmLike> userFilmLike = filmLikeStorage.userFilmLike(userId);
        List<FilmLike> filmLikesUsersWithCommonLike = filmLikeStorage.filmLikesUsersWithCommonLike(userId);
        // Мапа с id пользователей и количеством совпадений с лайками пользователя
        Map<Long, Integer> map = new HashMap<>();
        List<Long> userFilmIds = userFilmLike.stream()
                .map(FilmLike::getFilmId)
                .collect(Collectors.toList());
        filmLikesUsersWithCommonLike.forEach(filmLike -> {
            if (userFilmIds.contains(filmLike.getFilmId())) {
                map.merge(filmLike.getUserId(), 1, Integer::sum);
            }
        });
        List<Map.Entry<Long, Integer>> entrySetList = new ArrayList<>(map.entrySet());
        entrySetList.sort(Map.Entry.<Long, Integer>comparingByValue().reversed());
        List<FilmLike> userFriendsListWithoutUserFilms = filmLikesUsersWithCommonLike.stream()
                .filter(filmLike -> !userFilmIds.contains(filmLike.getFilmId()))
                .collect(Collectors.toList());
        List<Long> recommendedFilmIds = new ArrayList<>();
        entrySetList.forEach(entry -> {
            userFriendsListWithoutUserFilms.stream()
                    .filter(filmLike -> Objects.equals(filmLike.getUserId(), entry.getKey()))
                    .forEach(filmLike -> {
                        if (!recommendedFilmIds.contains(filmLike.getFilmId())) {
                            recommendedFilmIds.add(filmLike.getFilmId());
                        }
                    });
        });
        List<Film> filmsByIds = filmStorage.findFilmsByIds(recommendedFilmIds);
        enrichFilmGenres(filmsByIds);
        enrichFilmDirectors(filmsByIds);
        return filmsByIds;
    }


    private void enrichFilmGenres(List<Film> filmList) {
        List<Long> filmIds = filmList.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        List<FilmGenre> filmGenreList = filmGenreStorage.findFilmGenreByFilmIds(filmIds);
        List<Integer> genreIds = filmGenreList.stream()
                .map(FilmGenre::getGenreId)
                .collect(Collectors.toList());
        List<Genre> genres = genreStorage.findGenresByIds(genreIds);
        Map<Integer, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, List<Integer>> filmIdGenreIdsMap = new HashMap<>();
        filmGenreList.forEach(filmGenre -> {
            if (!filmIdGenreIdsMap.containsKey(filmGenre.getFilmId())) {
                filmIdGenreIdsMap.put(filmGenre.getFilmId(), filmGenreList.stream()
                        .filter(filmGenre1 -> filmGenre1.getFilmId().equals(filmGenre.getFilmId()))
                        .map(FilmGenre::getGenreId)
                        .collect(Collectors.toList()));
            }
        });
        filmList.forEach(film -> {
            List<Genre> genreList = new ArrayList<>();
            filmIdGenreIdsMap.getOrDefault(film.getId(), new ArrayList<>()).forEach(genreId -> genreList.add(genreMap.get(genreId)));
            film.setGenres(genreList);
        });
    }

    private void enrichFilmDirectors(List<Film> filmList) {
        List<Long> filmIds = filmList.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        List<FilmDirector> filmDirectorList = filmDirectorStorage.findFilmDirectorByFilmIds(filmIds);
        List<Long> directorsIds = filmDirectorList.stream()
                .map(FilmDirector::getDirectorId)
                .collect(Collectors.toList());
        List<Director> directors = directorStorage.findDirectorsByIds(directorsIds);
        Map<Long, Director> directorMap = directors.stream()
                .collect(Collectors.toMap(Director::getId, Function.identity()));
        Map<Long, List<Long>> filmIdDirectorsIdsMap = new HashMap<>();
        filmDirectorList.forEach(filmDirector -> {
            if (!filmIdDirectorsIdsMap.containsKey(filmDirector.getFilmId())) {
                filmIdDirectorsIdsMap.put(filmDirector.getFilmId(), filmDirectorList.stream()
                        .filter(filmDirector1 -> filmDirector1.getFilmId().equals(filmDirector.getFilmId()))
                        .map(FilmDirector::getDirectorId)
                        .collect(Collectors.toList()));
            }
        });
        filmList.forEach(film -> {
            List<Director> directorList = new ArrayList<>();
            filmIdDirectorsIdsMap.getOrDefault(film.getId(), new ArrayList<>()).forEach(directorId -> directorList.add(directorMap.get(directorId)));
            film.setDirectors(directorList);
        });
    }

    //Проверка, что пришедшие айдишники режиссеров есть в базе
    private void validateDirectors(Film film) {
        List<Long> directorsIds = new ArrayList<>();
        if (Objects.nonNull(film.getDirectors())) {
            directorsIds = film.getDirectors().stream().map(Director::getId).collect(Collectors.toList());
        }
        List<Long> directorExistedIds = directorStorage.findAll().stream().map(Director::getId).collect(Collectors.toList());
        directorsIds.removeAll(directorExistedIds);

        if (!directorsIds.isEmpty()) {
            log.error("Не найден режиссёр id: {} ", directorsIds);
            throw new IncorrectParameterException("directors " + directorsIds);
        }
    }

    //Проверка, что пришедшие айдишники жанров и рейтинга есть в базе
    private void validateGenresAndMPA(Film film) {
        List<Integer> genreIds = new ArrayList<>();
        if (Objects.nonNull(film.getGenres())) {
            genreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        }
        List<Integer> genreExistedIds = genreStorage.findAll().stream().map(Genre::getId).collect(Collectors.toList());
        genreIds.removeAll(genreExistedIds);
        Integer mpaId = null;
        if (Objects.nonNull(film.getMpa())) {
            mpaId = film.getMpa().getId();
        }
        List<Integer> mpaExistedIds = ratingStorage.findAll().stream().map(RatingMpa::getId).collect(Collectors.toList());
        if (!genreIds.isEmpty()) {
            if (Objects.nonNull(mpaId) && !mpaExistedIds.contains(mpaId)) {
                log.error("Не найдены жанры: {} ", genreIds);
                log.error("Не найден рейтинг id: {} ", mpaId);
                throw new IncorrectParameterException("genres " + genreIds + ", mpa " + mpaId);
            }
            log.error("Не найдены жанры: {} ", genreIds);
            throw new IncorrectParameterException("genres " + genreIds);
        }
        if (Objects.nonNull(mpaId) && !mpaExistedIds.contains(mpaId)) {
            log.error("Не найден рейтинг id: {} ", mpaId);
            throw new IncorrectParameterException("mpa " + mpaId);
        }
    }
}
