package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmRepository filmRepository;

    public FilmController(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @GetMapping()
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        Film savedFilm = filmRepository.save(film);
        log.info("Фильм сохранён: {}", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film updateFilm = filmRepository.update(film);
        log.info("Фильм обновлён: {}", updateFilm);
        return updateFilm;
    }
}
