package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {
    // Инициализация Validator
    private static final Validator validator;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void shouldBeValidated() {
        Film film = new Film(null, "", "", LocalDate.of(1994, 1, 1), 100, null, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("name is empty")));

        Film film2 = new Film(null, "test",
                "test test test test test test testtesttesttesttesttesttest testtesttesttesttest " +
                        "testtesttesttesttesttest testtesttesttesttesttesttest testtesttesttesttest     " +
                        "testtesttesttesttesttesttest!!!!!! testtesttesttest  ",
                LocalDate.of(2999, 1, 1), 300, null, null);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(1, violations2.size());
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("description is more than 200 symbols")));

        Film film3 = new Film(null, "test", "", LocalDate.of(1800, 1, 1), 100, null, null);
        Set<ConstraintViolation<Film>> violations3 = validator.validate(film3);
        assertEquals(1, violations3.size());
        assertTrue(violations3.stream().anyMatch(elem -> elem.getMessage().equals("RealiseDate is after 28.12.1895")));

        Film film4 = new Film(null, "test", "", LocalDate.of(1994, 1, 1), 0, null, null);
        Set<ConstraintViolation<Film>> violations4 = validator.validate(film4);
        assertEquals(1, violations4.size());
        assertTrue(violations4.stream().anyMatch(elem -> elem.getMessage().equals("duration is not positive")));

        Film film5 = new Film(null, null, null, null, null, null, null);
        Set<ConstraintViolation<Film>> violations5 = validator.validate(film5);
        assertEquals(3, violations5.size());
        assertTrue(violations5.stream().anyMatch(elem -> elem.getMessage().equals("duration is null")));
        assertTrue(violations5.stream().anyMatch(elem -> elem.getMessage().equals("releaseDate is null")));
    }
}
