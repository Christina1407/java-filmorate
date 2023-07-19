package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationType;

import java.util.List;

public interface FilmLikeStorage {
    void addLike(Long userId, Long filmId);
    void deleteLike(Long userId, Long filmId);
    List<Long> whoLikeFilm(Long filmId);

}
