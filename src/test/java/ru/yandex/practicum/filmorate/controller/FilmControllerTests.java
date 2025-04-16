package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void createUserIsValidateData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(120);
        Film createdFilm = filmController.create(film);
        assertNotNull(createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
        assertEquals("Test Description", createdFilm.getDescription());
        assertEquals(120, createdFilm.getDuration());
        assertEquals(LocalDate.of(2020, 2, 4), createdFilm.getReleaseDate());
    }

    @Test
    public void createUserIsEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Название не может быть пустым!", exception.getMessage());
    }

    @Test
    public void createUserIsIncorrectDescription() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("1Фильм — это искусство, которое сочетает в себе визуальные образы, звук и сюжет. Он способен вызывать эмоции, погружать зрителя в различные миры и рассказывать истории, отражающие человеческую природу.");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Максимальная длина описания — 200 символов!", exception.getMessage());
    }

    @Test
    public void createUserIsIncorrectReleaseData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1894, 2, 4));
        film.setDuration(120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года!", exception.getMessage());
    }

    @Test
    public void createUserIsIncorrectDuration() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 2, 4));
        film.setDuration(-120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertEquals("Продолжительность фильма должна быть положительным числом!", exception.getMessage());
    }

}
