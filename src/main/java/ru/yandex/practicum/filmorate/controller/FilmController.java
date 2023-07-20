package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
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
    public List<FilmDto> findAll() {
        List<Film> allFilms = filmService.getAllFilms();
        return FilmMapper.map(allFilms);
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
    public FilmDto findFilm(@PathVariable("filmId") Long filmId) {
        Film film = filmService.findFilmById(filmId);
        return FilmMapper.map(film);
    }

    @GetMapping("popular")
    public List<FilmDto> findPopular(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return FilmMapper.map(filmService.popularFilms(count));
    }

    @PutMapping("{filmId}/like/{userId}")
    public void addLike(@PathVariable("filmId") Long filmId,
                        @PathVariable("userId") Long userId) {
        filmService.addLike(filmId, userId);
        log.info("Пользователь id = : " + userId + " поставил like фильму id = " + filmId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void deleteLike(@PathVariable("filmId") Long filmId,
                           @PathVariable("userId") Long userId) {
        filmService.deleteLike(filmId, userId);
        log.info("Пользователь id = : " + userId + " удалил like фильму id = " + filmId);
    }
}
