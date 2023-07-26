package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> findAll();

    Genre findGenreById(int genreId);

    List<Genre> findGenresByIds(List<Integer> genreIds);
}
