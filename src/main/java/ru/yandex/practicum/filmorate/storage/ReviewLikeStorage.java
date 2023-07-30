package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ReviewLikeStorage {
    void addLikeDislike(Long reviewId, Long userId, boolean isLike);

    void deleteLikeDislike(Long reviewId, Long userId, boolean isLike);

    Integer sumLikeDislike(Long reviewId);

    List<Long> whoLikeReview(Long reviewId);

    List<Long> whoDislikeReview(Long reviewId);
}
