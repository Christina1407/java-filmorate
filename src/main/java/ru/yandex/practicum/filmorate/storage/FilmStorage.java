package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;

import java.util.List;

public interface FilmStorage {
    Film save(Film film);

    void delete(Long filmId);

    Film update(Film film);

    List<Film> findAll();

    Film findFilmById(Long filmId);

    List<Film> searchFilms(String query, List<String> searchByParams);

    List<Film> findFilmsByDirector(Long directorId, EnumSortBy sortBy);
}
