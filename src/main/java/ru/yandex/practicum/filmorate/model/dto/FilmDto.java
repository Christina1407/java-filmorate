package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class FilmDto {
    private Long id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
    private final RatingMpaDto mpa;
    private List<Genre> genres;
}
