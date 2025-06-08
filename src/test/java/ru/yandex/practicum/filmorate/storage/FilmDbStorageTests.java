package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmDbStorageTests {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Autowired
    private GenreDbStorage genreDbStorage;

    private Long mpaId;
    private List<Long> genreIds;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM film_genre");
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM genres");
        jdbc.update("DELETE FROM mpa");

        Mpa mpa = new Mpa();
        mpa.setMpa_name("PG-13");
        mpaDbStorage.create(mpa);
        mpaId = mpaDbStorage.getMpa().stream().findFirst().get().getMpa_id();

        Genre genre1 = new Genre();
        genre1.setName("Comedy");
        genreDbStorage.create(genre1);
        Genre genre2 = new Genre();
        genre2.setName("Drama");
        genreDbStorage.create(genre2);
        genreIds = genreDbStorage.getGenre().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
    }

    @Test
    void testCreateFilm() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
        film.setMpa(mpaId);
        film.setGenre(genreIds);
        film.setLikes(new HashSet<>());

        Film created = filmDbStorage.create(film);
        assertNotNull(created.getId());
        assertEquals("Inception", created.getName());
        assertEquals(mpaId, created.getMpa());
        assertEquals(genreIds.size(), created.getGenre().size());
    }

    @Test
    void testGetFilms() {
        testCreateFilm();

        Collection<Film> films = filmDbStorage.getFilms();
        assertFalse(films.isEmpty());
    }

    @Test
    void testGetFilmById() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Sci-fi action");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(mpaId);
        film.setGenre(genreIds);
        film.setLikes(new HashSet<>());

        Film created = filmDbStorage.create(film);

        Optional<Film> found = filmDbStorage.getFilmById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Matrix", found.get().getName());
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Original Title");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(mpaId);
        film.setGenre(genreIds);
        film.setLikes(new HashSet<>());

        Film created = filmDbStorage.create(film);

        created.setName("Updated Title");
        created.setDuration(110);
        Film updated = filmDbStorage.update(created);

        Optional<Film> found = filmDbStorage.getFilmById(updated.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getName());
        assertEquals(110, found.get().getDuration());
    }

    @Test
    void testSaveAndGetGenres() {
        Film film = new Film();
        film.setName("Genre Test Film");
        film.setDescription("Film with genres");
        film.setReleaseDate(LocalDate.of(2015, 5, 15));
        film.setDuration(140);
        film.setMpa(mpaId);
        film.setGenre(new ArrayList<>());
        film.setLikes(new HashSet<>());

        Film created = filmDbStorage.create(film);
        Long filmId = created.getId();

        filmDbStorage.saveGenres(filmId, genreIds);

        List<Long> genresFromDb = filmDbStorage.getGenresByFilmId(filmId);
        assertEquals(genreIds.size(), genresFromDb.size());
        assertTrue(genresFromDb.containsAll(genreIds));
    }
}
