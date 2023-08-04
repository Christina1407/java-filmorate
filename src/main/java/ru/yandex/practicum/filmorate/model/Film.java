package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.RealiseDateConstraints;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class Film {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private Long id;
    @NotBlank(message = "name is empty")
    private final String name;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "description is more than 200 symbols")
    private final String description;
    @NotNull(message = "releaseDate is null")
    @RealiseDateConstraints
    private final LocalDate releaseDate;
    @NotNull(message = "duration is null")
    @Positive(message = "duration is not positive")
    private final Integer duration;
    private final RatingMpa mpa;
    private List<Genre> genres;
    private List<Director> directors;
}
