package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.RealiseDateContraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.util.Constant.MAX_LENGTH_DESCRIPTION;

@Data
public class Film {
    private Integer id;
    @NotEmpty
    private final String name;
    @Size(max = MAX_LENGTH_DESCRIPTION)
    private final String description;
    @RealiseDateContraint
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
}
