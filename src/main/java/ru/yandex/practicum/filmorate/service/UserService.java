package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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
        return userStorage.getUserById(id);
    }

    // Добавляем друга (создаем заявку на дружбу)
    public User addFriend(Long userId, Long friendId) {
        log.info("Добавляем в друзья пользователя {} к пользователю {}.", friendId, userId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        friendshipStorage.addFriend(userId, friendId);

        return userStorage.getUserById(userId);
    }

    public User confirmFriend(Long userId, Long friendId) {
        log.info("Подтверждаем дружбу между пользователями {} и {}.", userId, friendId);

        friendshipStorage.confirmFriend(userId, friendId);

        return userStorage.getUserById(userId);
    }

    public User removeFriend(Long userId, Long friendId) {
        log.info("Удаляем из друзей пользователя {} у пользователя {}.", friendId, userId);

        friendshipStorage.removeFriend(userId, friendId);

        return userStorage.getUserById(userId);
    }

    public List<User> getFriends(Long userId) {
        log.info("Возвращаем друзей пользователя: {}", userId);

        return friendshipStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Возвращаем общих друзей пользователей {} и {}.", userId, otherUserId);

        return friendshipStorage.getCommonFriends(userId, otherUserId);
    }
}

