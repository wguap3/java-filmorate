package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
public class GenreDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM genres");
    }

    @Test
    void testCreateAndGetGenre() {
        Genre genre = new Genre();
        genre.setName("Comedy");

        Genre created = genreDbStorage.create(genre);
        assertNotNull(created);
        assertNotNull(created.getId());

        Collection<Genre> allGenres = genreDbStorage.getGenre();
        assertFalse(allGenres.isEmpty());
        assertTrue(allGenres.stream().anyMatch(g -> g.getName().equals("Comedy")));
    }

    @Test
    void testGetGenreById() {
        jdbcTemplate.update("INSERT INTO genres (genre_name) VALUES (?)", "Drama");
        Long id = jdbcTemplate.queryForObject("SELECT genre_id FROM genres WHERE genre_name = ?", Long.class, "Drama");

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(id);
        assertTrue(optionalGenre.isPresent());
        assertEquals("Drama", optionalGenre.get().getName());
    }

    @Test
    void testUpdateGenre() {
        jdbcTemplate.update("INSERT INTO genres (genre_name) VALUES (?)", "Horror");
        Long id = jdbcTemplate.queryForObject("SELECT genre_id FROM genres WHERE genre_name = ?", Long.class, "Horror");

        Genre genreToUpdate = new Genre();
        genreToUpdate.setId(id);
        genreToUpdate.setName("Thriller");

        Genre updated = genreDbStorage.update(genreToUpdate);
        assertEquals("Thriller", updated.getName());

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(id);
        assertTrue(optionalGenre.isPresent());
        assertEquals("Thriller", optionalGenre.get().getName());
    }

    @Test
    void testDeleteGenre() {
        jdbcTemplate.update("INSERT INTO genres (genre_name) VALUES (?)", "Sci-Fi");
        Long id = jdbcTemplate.queryForObject("SELECT genre_id FROM genres WHERE genre_name = ?", Long.class, "Sci-Fi");

        genreDbStorage.delete(id);

        Optional<Genre> optionalGenre = genreDbStorage.getGenreById(id);
        assertFalse(optionalGenre.isPresent());
    }
}
