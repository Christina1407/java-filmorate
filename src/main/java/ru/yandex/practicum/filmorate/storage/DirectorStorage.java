package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findDirectorById(Long directorId);

    List<Director> findDirectorsByIds(List<Long> directorsIds);

    Director save(Director director);

    Director update(Director director);

    void delete(Long directorId);
}

