package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotEmpty(message = "Название не может быть пустым!")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов!")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом!")
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
}
