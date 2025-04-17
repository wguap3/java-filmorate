package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTests {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validFilmShouldPassValidation() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }


    @Test
    public void emptyNameShouldFailValidation() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Название не может быть пустым!")));
    }


    @Test
    public void tooEarlyReleaseDateShouldFailValidation() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1894, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата выпуска не может быть в будущем.")));
    }

    @Test
    public void negativeDurationShouldFailValidation() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("положительным числом")));
    }

}
