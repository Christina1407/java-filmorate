package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingService {
    List<RatingMpa> getAllRatings();

    RatingMpa findRatingById(int ratingId);
}
