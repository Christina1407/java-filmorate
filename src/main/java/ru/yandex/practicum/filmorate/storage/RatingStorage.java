package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingStorage {
    List<RatingMpa> findAll();

    RatingMpa findRatingById(int ratingId);
}
