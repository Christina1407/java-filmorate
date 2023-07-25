package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Objects;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    @Override
    public Genre findGenreById(int genreId) {
        Genre genreById = genreStorage.findGenreById(genreId);
        if (Objects.isNull(genreById)) {
            throw new NotFoundException();
        }
        return genreById;
    }
}
