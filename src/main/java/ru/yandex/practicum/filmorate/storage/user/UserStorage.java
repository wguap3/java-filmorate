package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getUsers();

    User create(User user);

    User update(User newUser);

    void delete(Long id);

    Optional<User> getUserById(Long id);

}
