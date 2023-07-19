package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre findGenreById(int genreId);
}
