package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;

public interface FilmDirectorStorage {
    List<FilmDirector> findFilmDirectorByFilmIds(List<Long> filmIds);

    void insertIntoFilmDirectors(Film film);

    void updateFilmDirectors(Film film);

}
