package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    List<Review> getAllReviews();

    Review findReviewById(Long reviewId);

    Review saveReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    List<Review> findReviewsByFilmId(Long filmId);
}
