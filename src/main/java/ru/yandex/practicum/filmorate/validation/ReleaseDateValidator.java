package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate MIN_DATA_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(MIN_DATA_RELEASE);
    }
}
