package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewLike {
    private final long id;
    private final long reviewId;
    private final Boolean isLike;
}
