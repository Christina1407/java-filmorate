package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    // Инициализация Validator
    private static final Validator validator;
    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }
    @Test
    void shouldBeValidated() {
        User user = new User("test", "", LocalDate.of(1994, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("должно иметь формат адреса электронной почты")));
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("не должно быть пустым")));

        User user2 = new User("test@test.com", null, LocalDate.of(2999, 1, 1));
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(2, violations2.size());
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("должно содержать прошедшую дату")));
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("не должно быть пустым")));
    }

}
