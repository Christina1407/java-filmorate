package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;
import java.util.Objects;

@Service
public class RatingServiceImpl implements RatingService{
    private final RatingStorage ratingStorage;

    public RatingServiceImpl(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    @Override
    public List<RatingMpa> getAllRatings() {
       return ratingStorage.findAll();
    }

    @Override
    public RatingMpa findRatingById(int ratingId) {
        RatingMpa ratingById = ratingStorage.findRatingById(ratingId);
        if (Objects.isNull(ratingById)) {
            throw new NotFoundException();
        }
        return ratingById;
    }
}
