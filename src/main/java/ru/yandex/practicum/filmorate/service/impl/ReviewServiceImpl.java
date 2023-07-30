package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewLikeStorage reviewLikeStorage;


    public ReviewServiceImpl(ReviewStorage reviewStorage, UserService userService, FilmService filmService, ReviewLikeStorage reviewLikeStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.reviewLikeStorage = reviewLikeStorage;
    }

    @Override
    public List<Review> findAllReviews() {
        List<Review> reviews = reviewStorage.getAllReviews();
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    private void enrichReviewsByUseful(List<Review> allReviews) {
        allReviews.forEach(review -> review.setUseful(
                Optional.ofNullable(reviewLikeStorage.sumLikeDislike(review.getReviewId())).orElse(0)));
        allReviews.sort(Comparator.comparingInt(Review::getUseful).reversed());
    }

    private void enrichReviewsByUseful(Review review) {
        review.setUseful(Optional.ofNullable(reviewLikeStorage.sumLikeDislike(review.getReviewId())).orElse(0));
    }


    @Override
    public Review findReviewById(Long reviewId) {
        Review reviewById = reviewStorage.findReviewById(reviewId);
        if (Objects.nonNull(reviewById)) {
            enrichReviewsByUseful(reviewById);
            return reviewById;
        } else {
            log.error("Отзыв не найден id = {}", reviewId);
            throw new NotFoundException();
        }
    }

    @Override
    public Review saveReview(Review review) {
        //проверка что айди фильма и юзера существуют
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        enrichReviewsByUseful(review);
        return reviewStorage.saveReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        //айди фильма и юзера не могут поменяться при обновлении
//        userService.findUserById(review.getUserId());
//        filmService.findFilmById(review.getFilmId());
        enrichReviewsByUseful(review);
        return reviewStorage.updateReview(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        findReviewById(reviewId);
        reviewStorage.deleteReview(reviewId);

    }

    @Override
    public List<Review> findReviewsByFilmId(Long filmId, Integer count) {
        filmService.findFilmById(filmId);
        List<Review> reviews = reviewStorage.findReviewsByFilmId(filmId).stream()
                .limit(count)
                .collect(Collectors.toList());
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    @Override
    public List<Review> findReviewsByFilmId(Long filmId) {
        filmService.findFilmById(filmId);
        List<Review> reviewsByFilmId = reviewStorage.findReviewsByFilmId(filmId);
        enrichReviewsByUseful(reviewsByFilmId);
        return reviewsByFilmId;
    }

    @Override
    public List<Review> findReviewsWithCount(Integer count) {
        List<Review> reviews = findAllReviews().stream()
                .limit(count)
                .collect(Collectors.toList());
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    @Override
    public void addLikeDislike(Long reviewId, Long userId, Boolean isLike) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        List<Long> whoLikeReview = reviewLikeStorage.whoLikeReview(reviewId);
        List<Long> whoDislikeReview = reviewLikeStorage.whoDislikeReview(reviewId);
        if (isLike) { //прилетел лайк
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) { //проверяем, что от этого юзера нет лайка или дизлайка
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //добавляем лайк
            } else if (!whoLikeReview.contains(userId) && whoDislikeReview.contains(userId)) {//если есть дизлайк, то
                deleteLikeDislike(reviewId, userId, false); //удаляем дизлайк
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //ставим лайк
            }
        }
        if (!isLike) {// прилетел дизлайк
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                reviewLikeStorage.addLikeDislike(reviewId, userId, false);
            } else if (whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                deleteLikeDislike(reviewId, userId, true); //удаляем лайк
                reviewLikeStorage.addLikeDislike(reviewId, userId, false); //ставим дизлайк
            }
        }
    }

    @Override
    public void deleteLikeDislike(Long reviewId, Long userId, Boolean isLike) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        //при удалении лайка/дизлайка, которого нет, ничего не происходит, ошибка не выдаётся
        reviewLikeStorage.deleteLikeDislike(reviewId, userId, isLike);
    }
}
