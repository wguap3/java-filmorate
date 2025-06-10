package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Repository
@Qualifier("friendshipDbStorage")
public class FriendshipsDbStorage extends BaseStorage<Friendship> implements FriendshipStorage {

    private static final String INSERT_QUERY =
            "INSERT INTO friendships (first_user_id, second_user_id, status) VALUES (?, ?, ?)";
    private static final String CONFIRM_QUERY =
            "UPDATE friendships SET status = 'CONFIRMED' WHERE first_user_id = ? AND second_user_id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
    private static final String SELECT_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friendships f ON u.user_id = f.second_user_id " +
                    "WHERE f.first_user_id = ?";
    private static final String SELECT_COMMON_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN friendships f1 ON u.user_id = f1.second_user_id " +
                    "JOIN friendships f2 ON u.user_id = f2.second_user_id " +
                    "WHERE f1.first_user_id = ? AND f2.first_user_id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM friendships";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

    private final RowMapper<User> userMapper;

    @Autowired
    public FriendshipsDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> friendshipMapper, RowMapper<User> userMapper) {
        super(jdbc, friendshipMapper);
        this.userMapper = userMapper;
    }

    @Override
    public User addFriend(Long requesterId, Long targetId) {
        jdbc.update(INSERT_QUERY, requesterId, targetId, Friendship.FriendshipStatus.UNCONFIRMED.name());
        return findUserById(targetId);
    }

    @Override
    public User confirmFriend(Long requesterId, Long targetId) {
        jdbc.update(CONFIRM_QUERY, requesterId, targetId);
        return findUserById(targetId);
    }

    @Override
    public User removeFriend(Long userId, Long friendId) {
        jdbc.update(DELETE_QUERY, userId, friendId);
        return findUserById(friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbc.query(SELECT_FRIENDS_QUERY, userMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return jdbc.query(SELECT_COMMON_FRIENDS_QUERY, userMapper, userId, otherUserId);
    }

    @Override
    public List<Friendship> getAllFriendships() {
        return findMany(SELECT_ALL_QUERY);
    }

    private User findUserById(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }
}
