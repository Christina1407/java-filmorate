package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmService filmService;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        Film savedFilm = filmService.saveFilm(film);
        log.info("Фильм сохранён: {}", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film updateFilm = filmService.updateFilm(film);
        log.info("Фильм обновлён: {}", updateFilm);
        return updateFilm;
    }
    @GetMapping("{filmId}")
    public Film findUser(@PathVariable("filmId") Long filmId) {
        return filmService.findFilmById(filmId);
    }
    @GetMapping("popular")
    public List<Film> findPopular(
            @RequestParam(defaultValue = "10") Integer count) {
        if(count <=0 ) {
            throw new IncorrectParameterException("count");
        }
        return filmService.popularFilms(count);
    }

}
