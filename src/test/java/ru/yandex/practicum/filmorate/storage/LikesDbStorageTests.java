package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikesDbStorage.class})
public class LikesDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final LikesDbStorage likesDbStorage;

    private Long userId1;
    private Long userId2;
    private Long filmId1;
    private Long filmId2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM films");

        jdbcTemplate.update("INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)",
                "user1", "User One", "user1@example.com", Date.valueOf("1990-01-01"));
        jdbcTemplate.update("INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)",
                "user2", "User Two", "user2@example.com", Date.valueOf("1991-01-01"));

        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Film One", "Description One", Date.valueOf("2020-01-01"), 120);
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                "Film Two", "Description Two", Date.valueOf("2021-01-01"), 90);


        userId1 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = ?", Long.class, "user1");
        userId2 = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE login = ?", Long.class, "user2");


        filmId1 = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = ?", Long.class, "Film One");
        filmId2 = jdbcTemplate.queryForObject("SELECT film_id FROM films WHERE name = ?", Long.class, "Film Two");
    }

    @Test
    void testCreateLike() {
        Like like = new Like();
        like.setFilm_id(filmId1);
        like.setUser_id(userId1);

        Like created = likesDbStorage.create(like);

        assertNotNull(created);
        assertEquals(filmId1, created.getFilm_id());
        assertEquals(userId1, created.getUser_id());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId1, userId1);

        assertEquals(1, count);
    }

    @Test
    void testGetLikes() {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId1, userId1);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId2, userId2);

        Collection<Like> likes = likesDbStorage.getLikes();

        assertNotNull(likes);
        assertTrue(likes.size() >= 2);
    }

    @Test
    void testGetLikeById() {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId1, userId1);

        Optional<Like> likeOpt = likesDbStorage.getLikeById(filmId1);

        assertTrue(likeOpt.isPresent());
        Like like = likeOpt.get();
        assertEquals(filmId1, like.getFilm_id());
        assertEquals(userId1, like.getUser_id());
    }

    @Test
    void testDeleteLike() {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId1, userId1);

        likesDbStorage.delete(filmId1);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ?",
                Integer.class, filmId1);

        assertEquals(0, count);
    }
}


