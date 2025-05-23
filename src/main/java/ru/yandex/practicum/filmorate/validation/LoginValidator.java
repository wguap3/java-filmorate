package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<ValidLogin, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.contains(" ");
    }
}