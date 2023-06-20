package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(film) && Objects.nonNull(user)) {
            film.addLIke(userId);
        } else {
            throw new NotFoundException();
        }

    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(film) && Objects.nonNull(user)) {
            film.deleteLike(userId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public List<Film> popularFilms(Integer count) {
        Comparator<Film> comparator = (film1, film2) -> {
            if (film1.getWhoLikeId().isEmpty() && film2.getWhoLikeId().isEmpty()) {
                return Math.toIntExact(film2.getId() - film1.getId());
            } else {
                return film2.getWhoLikeId().size() - film1.getWhoLikeId().size();
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
        return filmStorage.update(film);
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
