package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Review {
    private static final int MAX_LENGTH_CONTENT = 2000;
    private Long reviewId;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    @Size(max = MAX_LENGTH_CONTENT, message = "content is more than 2000 symbols")
    private final String content;
    @NotNull(message = "creationDate is null")
    private final LocalDate creationDate;
    private final Boolean isPositive; //положительный или отрицательный отзыв

    public Review(Long reviewId, Long userId, Long filmId, String content, LocalDate creationDate, Boolean isPositive) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
        this.creationDate = LocalDate.now();
    }
}
