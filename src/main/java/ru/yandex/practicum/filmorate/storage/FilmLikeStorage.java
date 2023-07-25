package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FilmLikeStorage {
    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);

    List<Long> whoLikeFilm(Long filmId);

}
