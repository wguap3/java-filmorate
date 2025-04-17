package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidLogin;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    @Email(message = "Электронная почта должна быть корректной.")
    @NotEmpty(message = "Электронная почта не может быть пустой.")
    private String email;
    @NotEmpty(message = "Логин не может быть пустым.")
    @ValidLogin
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
