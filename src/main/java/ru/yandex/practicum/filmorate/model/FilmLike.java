package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmLike {
    private final long id;
    private final long filmId;
    private final long userId;
}
