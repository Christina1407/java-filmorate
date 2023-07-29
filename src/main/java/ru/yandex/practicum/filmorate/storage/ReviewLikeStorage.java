package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikeStorage {
    void addLikeDislike(Long reviewId, Long userId, boolean isLike);

    void deleteLikeDislike(Long reviewId, Long userId, boolean isLike);

    Integer sumLikeDislike(Long reviewId);
}
