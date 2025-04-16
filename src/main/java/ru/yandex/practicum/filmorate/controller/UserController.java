package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь создан: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            throw new ValidationException("Пользователь не найден!");
        }
        try {
            if (newUser.getName() != null) {
                existingUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null) {
                existingUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                existingUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                existingUser.setBirthday(newUser.getBirthday());
            }
            validateUser(existingUser);
            log.info("Пользователь обновлён: {}", existingUser);
            return existingUser;
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
