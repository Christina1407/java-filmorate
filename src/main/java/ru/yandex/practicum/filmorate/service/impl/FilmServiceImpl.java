package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
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


    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, FilmLikeStorage filmLikeStorage, FilmGenreStorage filmGenreStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
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
    public List<Film> popularFilms(Integer count) {
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
        List<Film> filmList = filmStorage.findAll().stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
        enrichFilmGenres(filmList);
        return filmList;
    }


    @Override
    public Film saveFilm(Film film) {
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        Film update = filmStorage.update(film);
        if (Objects.isNull(update)) {
            log.error("Фильм не найден");
            throw new NotFoundException();
        }
        enrichFilmGenres(List.of(update));
        return update;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList = filmStorage.findAll();
        enrichFilmGenres(filmList);
        return filmList;
    }

    @Override
    public Film findFilmById(Long filmId) {
        Film filmById = filmStorage.findFilmById(filmId);
        if (Objects.nonNull(filmById)) {
            enrichFilmGenres(List.of(filmById));
            return filmById;
        } else {
            throw new NotFoundException();
        }
    }

    private void enrichFilmGenres(List<Film> filmList) {
        List<Long> filmIds = filmList.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        List<FilmGenre> filmGenreList = filmGenreStorage.findFilmGenreByFilmIds(filmIds);
        List<Integer> genreIds = filmGenreList.stream()
                .map(FilmGenre::getGenreId)
                .collect(Collectors.toList());
        List<Genre> genres = genreStorage.findGenreByIds(genreIds);
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
}
