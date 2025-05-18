package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Set<Friendship>> friends = new HashMap<>();
    private final UserStorage userStorage;

    private final AtomicLong friendshipIdCounter = new AtomicLong(1);

    @Autowired
    public InMemoryFriendshipStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User addFriend(Long firstUserId, Long secondUserId) {
        if (!friends.containsKey(firstUserId)) {
            friends.put(firstUserId, new HashSet<>());
        }
        Friendship friendship = new Friendship();
        friendship.setId(friendshipIdCounter.getAndIncrement());
        friendship.setFirstUserId(firstUserId);
        friendship.setSecondUserId(secondUserId);
        friendship.setStatus(Friendship.FriendshipStatus.UNCONFIRMED);

        friends.get(firstUserId).add(friendship);

        User user = userStorage.getUserById(firstUserId);
        user.getFriendshipIds().add(friendship.getId());
        userStorage.update(user);

        return userStorage.getUserById(firstUserId);
    }

    @Override
    public User confirmFriend(Long firstUserId, Long secondUserId) {
        Set<Friendship> firstUserFriends = friends.get(firstUserId);
        if (firstUserFriends != null) {
            for (Friendship f : firstUserFriends) {
                if (f.getSecondUserId().equals(secondUserId)) {
                    f.setStatus(Friendship.FriendshipStatus.CONFIRMED);
                    break;
                }
            }
        }

        if (!friends.containsKey(secondUserId)) {
            friends.put(secondUserId, new HashSet<>());
        }

        Friendship backFriendship = new Friendship();
        backFriendship.setId(friendshipIdCounter.getAndIncrement());
        backFriendship.setFirstUserId(secondUserId);
        backFriendship.setSecondUserId(firstUserId);
        backFriendship.setStatus(Friendship.FriendshipStatus.CONFIRMED);

        friends.get(secondUserId).add(backFriendship);

        User firstUser = userStorage.getUserById(firstUserId);
        firstUser.getFriendshipIds().add(
                friends.get(firstUserId).stream()
                        .filter(f -> f.getSecondUserId().equals(secondUserId))
                        .findFirst()
                        .map(Friendship::getId)
                        .orElse(null)
        );
        userStorage.update(firstUser);

        User secondUser = userStorage.getUserById(secondUserId);
        secondUser.getFriendshipIds().add(backFriendship.getId());
        userStorage.update(secondUser);

        return userStorage.getUserById(firstUserId);
    }

    @Override
    public User removeFriend(Long firstUserId, Long secondUserId) {
        if (friends.containsKey(firstUserId)) {
            friends.get(firstUserId).removeIf(f -> {
                boolean match = f.getSecondUserId().equals(secondUserId);
                if (match) {
                    User user = userStorage.getUserById(firstUserId);
                    user.getFriendshipIds().remove(f.getId());
                    userStorage.update(user);
                }
                return match;
            });
        }

        if (friends.containsKey(secondUserId)) {
            friends.get(secondUserId).removeIf(f -> {
                boolean match = f.getSecondUserId().equals(firstUserId);
                if (match) {
                    User user = userStorage.getUserById(secondUserId);
                    user.getFriendshipIds().remove(f.getId());
                    userStorage.update(user);
                }
                return match;
            });
        }

        return userStorage.getUserById(firstUserId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        Set<Friendship> userFriendships = friends.getOrDefault(userId, new HashSet<>());

        return userFriendships.stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.CONFIRMED)
                .map(f -> userStorage.getUserById(f.getSecondUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        Set<Long> user1Friends = friends.getOrDefault(userId, new HashSet<>()).stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.CONFIRMED)
                .map(Friendship::getSecondUserId)
                .collect(Collectors.toSet());

        Set<Long> user2Friends = friends.getOrDefault(otherUserId, new HashSet<>()).stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.CONFIRMED)
                .map(Friendship::getSecondUserId)
                .collect(Collectors.toSet());

        user1Friends.retainAll(user2Friends);

        return user1Friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Friendship> getAllFriendships() {
        return friends.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

}


