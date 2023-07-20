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
        User user = new User(null, "test", "", null, LocalDate.of(1994, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(3, violations.size());
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("email is not well-formed email address")));
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("login is empty")));
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("login with whitespaces")));

        User user2 = new User(null, "test@test.com", null, null, LocalDate.of(2999, 1, 1));
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(2, violations2.size());
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("birthday is not in past")));
        assertTrue(violations2.stream().anyMatch(elem -> elem.getMessage().equals("login is empty")));

        User user3 = new User(null, "test@test.com", "  test   test  ", null, LocalDate.of(1999, 1, 1));
        Set<ConstraintViolation<User>> violations3 = validator.validate(user3);
        assertEquals(1, violations3.size());
        assertTrue(violations3.stream().anyMatch(elem -> elem.getMessage().equals("login with whitespaces")));

        User user4 = new User(null, null, null, null, null);
        Set<ConstraintViolation<User>> violations4 = validator.validate(user4);
        assertEquals(2, violations4.size());
        assertTrue(violations4.stream().anyMatch(elem -> elem.getMessage().equals("email is empty")));

    }

}
