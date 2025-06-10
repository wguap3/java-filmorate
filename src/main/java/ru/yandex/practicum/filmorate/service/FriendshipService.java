package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipStorage;

import java.util.List;

@Service
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public FriendshipService(FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
    }

    public User addFriend(Long requesterId, Long targetId) {
        return friendshipStorage.addFriend(requesterId, targetId);
    }

    public User confirmFriend(Long requesterId, Long targetId) {
        return friendshipStorage.confirmFriend(requesterId, targetId);
    }

    public User removeFriend(Long userId, Long friendId) {
        return friendshipStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        return friendshipStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return friendshipStorage.getCommonFriends(userId, otherUserId);
    }

    public List<Friendship> getAllFriendships() {
        return friendshipStorage.getAllFriendships();
    }
}
