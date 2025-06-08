package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(FilmDbStorage.class)
class UserDbStorageTests {
    private final UserDbStorage userStorage;


    private User createTestUser() {
        User user = new User();
        user.setEmail("testuser@mail.ru");
        user.setLogin("testlogin");
        user.setName("Тестовое имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setFriendshipIds(new HashSet<>());
        return user;
    }

    @Test
    void testCreateUser() {
        User user = createTestUser();
        User createdUser = userStorage.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(createdUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(createdUser.getId()).isNotNull();
        Optional<User> userFromDb = userStorage.getUserById(createdUser.getId());
        assertThat(userFromDb).isPresent();
    }

    @Test
    void testGetUserById() {
        User user = createTestUser();
        User createdUser = userStorage.create(user);

        Optional<User> foundUser = userStorage.getUserById(createdUser.getId());

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getId()).isEqualTo(createdUser.getId());
                    assertThat(u.getEmail()).isEqualTo(createdUser.getEmail());
                });
    }

    @Test
    void testGetUsers() {
        User user1 = createTestUser();
        userStorage.create(user1);

        User user2 = createTestUser();
        user2.setEmail("another@mail.ru");
        userStorage.create(user2);

        Collection<User> users = userStorage.getUsers();

        assertThat(users).isNotEmpty();
        assertThat(users).extracting("email")
                .contains(user1.getEmail(), user2.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = createTestUser();
        User createdUser = userStorage.create(user);
        assertThat(createdUser.getId()).isNotNull();
        createdUser.setName("Updated Name");
        createdUser.setEmail("updated@mail.ru");
        User updatedUser = userStorage.update(createdUser);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.ru");
    }

    @Test
    void testDeleteUser() {
        User user = createTestUser();
        User createdUser = userStorage.create(user);
        assertThat(createdUser.getId()).isNotNull();
        userStorage.delete(createdUser.getId());
        Optional<User> userFromDb = userStorage.getUserById(createdUser.getId());
        assertThat(userFromDb).isNotPresent();
    }
}