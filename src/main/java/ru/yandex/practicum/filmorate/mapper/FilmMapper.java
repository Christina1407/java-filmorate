package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.RatingMpaDto;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {
    public static List<FilmDto> map(List<Film> films) {
        return films.stream()
                .map(FilmMapper::getBuild)
                .collect(Collectors.toList());
    }

    private static FilmDto getBuild(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .mpa(new RatingMpaDto(film.getMpa().getId(), film.getMpa().getName().getName()))
                .name(film.getName())
                .description(film.getDescription())
                .genres(film.getGenres())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    public static FilmDto map(Film film) {
        return getBuild(film);
    }
}
