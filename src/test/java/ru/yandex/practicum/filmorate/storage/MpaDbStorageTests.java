package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})
class MpaDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM mpa");
    }

    @Test
    void testCreateAndGetMpa() {
        Mpa mpa = new Mpa();
        mpa.setName("PG-13");

        Mpa created = mpaDbStorage.create(mpa);
        assertNotNull(created);

        Long id = jdbcTemplate.queryForObject("SELECT id FROM mpa WHERE name = ?", Long.class, "PG-13");
        assertNotNull(id);

        Collection<Mpa> allMpas = mpaDbStorage.getMpa();
        assertFalse(allMpas.isEmpty());
        assertTrue(allMpas.stream().anyMatch(m -> "PG-13".equals(m.getName())));
    }

    @Test
    void testGetMpaById() {
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "R");
        Long id = jdbcTemplate.queryForObject("SELECT id FROM mpa WHERE name = ?", Long.class, "R");

        Optional<Mpa> optionalMpa = mpaDbStorage.getMpaById(id);
        assertTrue(optionalMpa.isPresent(), "Mpa should be found by ID");
        assertEquals("R", optionalMpa.get().getName());
    }

    @Test
    void testUpdateMpa() {
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "PG");
        Long id = jdbcTemplate.queryForObject("SELECT id FROM mpa WHERE name = ?", Long.class, "PG");

        Mpa mpaToUpdate = new Mpa();
        mpaToUpdate.setId(id);
        mpaToUpdate.setName("PG-13");

        Mpa updated = mpaDbStorage.update(mpaToUpdate);
        assertEquals("PG-13", updated.getName());

        Optional<Mpa> optionalMpa = mpaDbStorage.getMpaById(id);
        assertTrue(optionalMpa.isPresent());
        assertEquals("PG-13", optionalMpa.get().getName());
    }

    @Test
    void testDeleteMpa() {
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "G");
        Long id = jdbcTemplate.queryForObject("SELECT id FROM mpa WHERE name = ?", Long.class, "G");

        mpaDbStorage.delete(id);

        Optional<Mpa> optionalMpa = mpaDbStorage.getMpaById(id);
        assertFalse(optionalMpa.isPresent());
    }
}



