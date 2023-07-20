package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EnumMPA;

@Data
public class RatingMpa {
    private final int id;
    private final EnumMPA name;
}
