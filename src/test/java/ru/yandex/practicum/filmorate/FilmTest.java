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
    // TODO переделать логику, логика скопирована из теста User
    @Test
    void shouldBeValidated() {
        Film film = new Film("test", "", LocalDate.of(1994, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("должно иметь формат адреса электронной почты")));
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("не должно быть пустым")));

        Film film2 = new Film("test@test.com", null, LocalDate.of(2999, 1, 1), 300);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        assertEquals(2, violations2.size());
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("должно содержать прошедшую дату")));
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("не должно быть пустым")));
    }
}
