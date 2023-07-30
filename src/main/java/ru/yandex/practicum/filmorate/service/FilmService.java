package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;

import java.util.List;

public interface FilmService {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> popularFilms(Integer count, Integer genreId, Integer year);

    Film saveFilm(Film film);

    void deleteFilm(Long filmId);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film findFilmById(Long filmId);

    List<Film> findFilmsByDirectorId(Long directorId, EnumSortBy sortBy);

    List<Film> searchFilms(String query, List<String> searchByParams);
}
