package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> findAllReviews();

    Review findReviewById(Long reviewId);

    Review saveReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    List<Review> findReviewsByFilmId(Long filmId, Integer count);

    List<Review> findReviewsByFilmId(Long filmId);

    List<Review> findReviewsWithCount(Integer count);

    void addLikeDislike(Long reviewId, Long userId, Boolean isLike);

    void deleteLikeDislike(Long reviewId, Long userId, Boolean isLike);


}

