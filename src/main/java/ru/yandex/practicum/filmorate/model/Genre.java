package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Genre {
    private final int id;
    @NotEmpty(message = "name is empty")
    private final String name;
}
