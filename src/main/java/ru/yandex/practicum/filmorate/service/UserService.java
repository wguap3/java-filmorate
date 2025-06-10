package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipsDbStorage friendshipsDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("friendshipsDbStorage") FriendshipsDbStorage friendshipsDbStorage) {
        this.userStorage = userStorage;
        this.friendshipsDbStorage = friendshipsDbStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }

    public User getUserById(Long id) {

        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }


    public User addFriend(Long userId, Long friendId) {
        log.info("Добавляем в друзья пользователя {} к пользователю {}.", friendId, userId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));


        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден."));


        friendshipsDbStorage.addFriend(userId, friendId);

        return userStorage.getUserById(userId)
                .orElseThrow(() -> new InternalServerException("Пользователь с id " + userId + " не найден."));
    }

    public User confirmFriend(Long userId, Long friendId) {
        log.info("Подтверждаем дружбу между пользователями {} и {}.", userId, friendId);

        friendshipsDbStorage.confirmFriend(userId, friendId);

        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    public User removeFriend(Long userId, Long friendId) {
        log.info("Удаляем из друзей пользователя {} у пользователя {}.", friendId, userId);

        friendshipsDbStorage.removeFriend(userId, friendId);

        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    public List<User> getFriends(Long userId) {
        log.info("Возвращаем друзей пользователя: {}", userId);
        userStorage.getUserById(userId);
        return friendshipsDbStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Возвращаем общих друзей пользователей {} и {}.", userId, otherUserId);

        return friendshipsDbStorage.getCommonFriends(userId, otherUserId);
    }
}

