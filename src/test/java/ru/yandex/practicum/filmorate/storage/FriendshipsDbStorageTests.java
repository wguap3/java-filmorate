package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendshipsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({FriendshipsDbStorage.class, UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipsDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final FriendshipsDbStorage friendshipsDbStorage;

    private Long userId1;
    private Long userId2;
    private Long userId3;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        insertUser("user1", "User One", "user1@example.com", Date.valueOf("1990-01-01"));
        insertUser("user2", "User Two", "user2@example.com", Date.valueOf("1991-01-01"));
        insertUser("user3", "User Three", "user3@example.com", Date.valueOf("1992-01-01"));

        userId1 = getUserIdByLogin("user1");
        userId2 = getUserIdByLogin("user2");
        userId3 = getUserIdByLogin("user3");
    }

    private void insertUser(String login, String name, String email, Date birthday) {
        jdbcTemplate.update(
                "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)",
                login, name, email, birthday
        );
    }

    private Long getUserIdByLogin(String login) {
        return jdbcTemplate.queryForObject(
                "SELECT user_id FROM users WHERE login = ?",
                Long.class,
                login
        );
    }

    @Test
    void testAddFriend() {
        var addedFriend = friendshipsDbStorage.addFriend(userId1, userId2);

        assertNotNull(addedFriend);
        assertEquals(userId2, addedFriend.getId());
    }

    @Test
    void testConfirmFriend() {
        friendshipsDbStorage.addFriend(userId1, userId2);

        var confirmedFriend = friendshipsDbStorage.confirmFriend(userId1, userId2);

        assertNotNull(confirmedFriend);
        assertEquals(userId2, confirmedFriend.getId());

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM friendships WHERE first_user_id = ? AND second_user_id = ?",
                String.class, userId1, userId2);
        assertEquals("CONFIRMED", status);
    }

    @Test
    void testRemoveFriend() {
        friendshipsDbStorage.addFriend(userId1, userId2);

        var removedFriend = friendshipsDbStorage.removeFriend(userId1, userId2);

        assertNotNull(removedFriend);
        assertEquals(userId2, removedFriend.getId());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE first_user_id = ? AND second_user_id = ?",
                Integer.class, userId1, userId2);
        assertEquals(0, count);
    }

    @Test
    void testGetFriends() {
        friendshipsDbStorage.addFriend(userId1, userId2);
        friendshipsDbStorage.addFriend(userId1, userId3);
        friendshipsDbStorage.confirmFriend(userId1, userId2);
        friendshipsDbStorage.confirmFriend(userId1, userId3);

        List<User> friends = friendshipsDbStorage.getFriends(userId1);

        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(userId2)));
        assertTrue(friends.stream().anyMatch(u -> u.getId().equals(userId3)));
    }

    @Test
    void testGetCommonFriends() {
        friendshipsDbStorage.addFriend(userId1, userId3);
        friendshipsDbStorage.confirmFriend(userId1, userId3);

        friendshipsDbStorage.addFriend(userId2, userId3);
        friendshipsDbStorage.confirmFriend(userId2, userId3);

        List<User> commonFriends = friendshipsDbStorage.getCommonFriends(userId1, userId2);

        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals(userId3, commonFriends.get(0).getId());
    }

    @Test
    void testGetAllFriendships() {
        friendshipsDbStorage.addFriend(userId1, userId2);
        friendshipsDbStorage.addFriend(userId2, userId3);

        List<Friendship> friendships = friendshipsDbStorage.getAllFriendships();

        assertNotNull(friendships);
        assertTrue(friendships.size() >= 2);
    }
}


