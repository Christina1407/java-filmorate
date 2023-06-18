package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.util.*;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
      private long id = 0;

    @Override
    public Film save(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (Objects.nonNull(film.getId())) {
            if (!films.containsKey(film.getId())) {
                log.error("Фильма с таким id не найдено: {}", film.getId());
                throw new ValidationException();
            } else {
                films.put(film.getId(), film);
            }
        } else {
            save(film);
        }

        return film;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(Long filmId) {
        return films.get(filmId);
    }
}
