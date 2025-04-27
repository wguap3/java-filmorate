package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        log.info("Добавляем  в друзья к  " + userId + "пользователя: " + friendId + ".");
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        log.info("Удаляем из друзей " + userId + "пользователя: " + friendId + ".");
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public List<User> getFriends(Long userId) {
        log.info("Возвращаем друзей пользователя: " + userId);
        User user = userStorage.getUserById(userId);
        Set<Long> friendsIds = new HashSet<>(user.getFriends());

        List<User> friends = new ArrayList<>();
        for (Long id : friendsIds) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Возвращаем общих друзей пользователей: " + userId + "и " + otherUserId);
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);

        Set<Long> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(otherUser.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Long id : commonIds) {
            commonFriends.add(userStorage.getUserById(id));
        }
        return commonFriends;
    }


}
