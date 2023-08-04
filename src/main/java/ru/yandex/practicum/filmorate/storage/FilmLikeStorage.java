package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;

public interface FilmLikeStorage {
    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);

    List<Long> usersIdsWhoLikeFilm(Long filmId);

    List<FilmLike> userFilmLike(Long userId);

    List<FilmLike> filmLikesUsersWithCommonLike(Long userId);
}
