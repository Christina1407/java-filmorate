package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeStorage = filmLikeStorage;
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
            if (filmLikeStorage.whoLikeFilm(film1.getId()).isEmpty() && filmLikeStorage.whoLikeFilm(film2.getId()).isEmpty()) {
                return Math.toIntExact(film2.getId() - film1.getId());
            } else {
                return filmLikeStorage.whoLikeFilm(film1.getId()).size() - filmLikeStorage.whoLikeFilm(film2.getId()).size();
            }
        };
        return filmStorage.findAll().stream()
                .sorted(comparator)
                .limit(count).collect(Collectors.toList());
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
        return update;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film findFilmById(Long filmId) {
        if (Objects.nonNull(filmStorage.findFilmById(filmId))) {
            return filmStorage.findFilmById(filmId);
        } else {
            throw new NotFoundException();
        }
    }
}
