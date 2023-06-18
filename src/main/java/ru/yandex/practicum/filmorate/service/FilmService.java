package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Boolean addLike(Long filmId, Long userId);
    Boolean deleteLike(Long filmId, Long userId);
    List<Film> popularFilms(Integer count);
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film findFilmById(Long filmId);
}
