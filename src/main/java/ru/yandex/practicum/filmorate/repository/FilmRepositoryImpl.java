package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.util.*;

@Repository
public class FilmRepositoryImpl implements FilmRepository{
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;
    @Override
    public Film save(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if(Objects.nonNull(film.getId())) {
            if (!films.containsKey(film.getId())) {
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
}
