package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.dto.RatingMpaDto;

import java.util.List;
import java.util.stream.Collectors;

public class RatingMpaMapper {
    public static List<RatingMpaDto> map(List<RatingMpa> ratingMpaList) {
        return ratingMpaList.stream()
                .map(ratingMpa -> new RatingMpaDto(ratingMpa.getId(), ratingMpa.getName().getName()))
                .collect(Collectors.toList());
    }
}
