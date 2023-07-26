package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
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
        List<Film> allFilms = filmService.getAllFilms();
        return allFilms;
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        Film savedFilm = filmService.saveFilm(film);
        log.info("Фильм сохранён: {}", savedFilm);
        return savedFilm;
    }

    @DeleteMapping("{filmId}")
    public void deleteUser(@PathVariable("filmId") Long filmId) {
        filmService.deleteFilm(filmId);
        log.info("Фильм id = : " + filmId + " удалён ");
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film updateFilm = filmService.updateFilm(film);
        log.info("Фильм обновлён: {}", updateFilm);
        return updateFilm;
    }

    @GetMapping("{filmId}")
    public Film findFilm(@PathVariable("filmId") Long filmId) {
        Film film = filmService.findFilmById(filmId);
        return film;
    }

    @GetMapping("popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.popularFilms(count);
    }

    //Функциональность "Популярные фильмы", которая предусматривает вывод самых любимых у зрителей фильмов по жанрам и годам
//    @GetMapping("popular/{year}/{genre}")
//    public List<Film> findPopularYearAndGenre(@PathVariable("year") Integer year,
//                                              @PathVariable("genre") String genre) {
//        if (count <= 0) {
//            throw new IncorrectParameterException("count");
//        }
//        return filmService.popularFilms(count);
//    }

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
