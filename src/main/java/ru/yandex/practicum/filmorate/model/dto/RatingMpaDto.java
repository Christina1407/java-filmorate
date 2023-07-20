package ru.yandex.practicum.filmorate.model.dto;

import lombok.Data;

//Сделано из-за дефиса в рейтинге MPA, так как в enum нельзя его поставить
@Data
public class RatingMpaDto {
    private final int id;
    private final String name;
}
