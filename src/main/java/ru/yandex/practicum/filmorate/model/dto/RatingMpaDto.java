package ru.yandex.practicum.filmorate.model.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.EnumMPA;

@Data
public class RatingMpaDto {
    private final int id;
    private final String name;
}
