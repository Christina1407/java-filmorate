package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RealiseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RealiseDateContraint {
    String message() default "Film realise must be after 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}