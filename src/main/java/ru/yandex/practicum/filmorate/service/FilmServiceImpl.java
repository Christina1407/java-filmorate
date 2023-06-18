package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Boolean addLike(Long filmId, Long userId) {
        return filmStorage.findFilmById(filmId).addLIke(userId);
    }

    @Override
    public Boolean deleteLike(Long filmId, Long userId) {
        return filmStorage.findFilmById(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> popularFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getWhoLikeId().size()))
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
        return filmStorage.findFilmById(filmId);
    }

}
