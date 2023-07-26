package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getAllDirectors();

    Director findDirectorById(Long directorId);

    Director saveDirector(Director director);

    Director updateDirector(Director director);
}
