package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Slf4j
public class RealiseDateValidator implements ConstraintValidator<RealiseDateConstraints, LocalDate> {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {   //так как есть аннотация NotNull
            return true;
        }
        boolean result = value.isAfter(MIN_RELEASE_DATE);
        if (!result) {
            log.error("RealiseDate раньше MIN_RELEASE_DATE: {}", MIN_RELEASE_DATE);
        }

        return result;
    }
}
