package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;


    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, FilmLikeStorage filmLikeStorage, FilmGenreStorage filmGenreStorage, GenreStorage genreStorage, RatingStorage ratingStorage, FilmDirectorStorage filmDirectorStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.filmDirectorStorage = filmDirectorStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(film) && Objects.nonNull(user)) {
            filmLikeStorage.addLike(userId, filmId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(film) && Objects.nonNull(user)) {
            filmLikeStorage.deleteLike(userId, filmId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public List<Film> popularFilms(Integer count, Integer genreId, Integer year) {
        Comparator<Film> comparator = (film1, film2) -> {
            List<Long> whoLikeFilm1 = filmLikeStorage.whoLikeFilm(film1.getId());
            List<Long> whoLikeFilm2 = filmLikeStorage.whoLikeFilm(film2.getId());
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
