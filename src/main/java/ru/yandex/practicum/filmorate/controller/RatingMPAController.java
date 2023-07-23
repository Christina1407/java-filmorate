package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingMPAController {
    RatingService ratingService;

    @Autowired
    public RatingMPAController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping()
    public List<RatingMpa> findAll() {
        return ratingService.getAllRatings();
    }

    @GetMapping("{ratingId}")
    public RatingMpa findUser(@PathVariable("ratingId") int ratingId) {
        return ratingService.findRatingById(ratingId);
    }
}
