package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping()
    public Director create(@Valid @RequestBody Director director) {
        Director savedDirector = directorService.saveDirector(director);
        log.info("Добавлен новый режиссёр: {}", savedDirector);
        return savedDirector;
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        Director updateDirector = directorService.updateDirector(director);
        log.info("Режиссёр обновлён : {}", updateDirector);
        return updateDirector;
    }

    @DeleteMapping("{directorId}")
    public void deleteDirector(@PathVariable("directorId") Long directorId) {
        directorService.deleteDirector(directorId);
        log.info("Режиссёр id = : " + directorId + " удалён ");
    }

    @GetMapping()
    public List<Director> findAll() {
        return directorService.getAllDirectors();
    }

    @GetMapping("{directorId}")
    public Director findDirectorById(@PathVariable("directorId") long directorId) {
        return directorService.findDirectorById(directorId);
    }
}
