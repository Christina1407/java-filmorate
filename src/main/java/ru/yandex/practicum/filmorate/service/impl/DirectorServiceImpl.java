package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.findAll();
    }

    @Override
    public Director findDirectorById(Long directorId) {
        if (Objects.nonNull(directorStorage.findDirectorById(directorId))) {
            return directorStorage.findDirectorById(directorId);
        } else {
            log.error("Режиссёр не найден id = {}", directorId);
            throw new NotFoundException();
        }
    }

    @Override
    public Director saveDirector(Director director) {
        return directorStorage.save(director);
    }

    @Override
    public Director updateDirector(Director director) {
        findDirectorById(director.getId());
        return directorStorage.update(director);
    }

    @Override
    public void deleteDirector(Long directorId) {
        findDirectorById(directorId);
        directorStorage.delete(directorId);
    }
}
