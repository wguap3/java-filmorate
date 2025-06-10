package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa = m.id";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa = m.id " +
                    "WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE film_id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Optional<Film> optFilm = findOne(FIND_BY_ID_QUERY, filmId);
        optFilm.ifPresent(film -> {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setLikes(getLikesByFilmId(film.getId()));
        });
        return optFilm;
    }

    @Override
    public Film create(Film film) {
        try {

            long id = insert(
                    INSERT_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa() != null ? film.getMpa().getId() : null
            );
            film.setId(id);
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                Mpa fullMpa = getMpaById(film.getMpa().getId());
                film.setMpa(fullMpa);
            }
            saveGenres(id, film.getGenres());
            saveLikes(id, film.getLikes());

            film.setGenres(getGenresByFilmId(id));
            film.setLikes(getLikesByFilmId(id));

            return film;
        } catch (DataIntegrityViolationException ex) {
            String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

            if (message.contains("CONSTRAINT_3F")) {
                throw new NotFoundException("MPA rating not found");
            } else if (message.contains("CONSTRAINT_79D")) {
                throw new NotFoundException("Genre not found");
            }
            throw ex;
        }
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Mpa fullMpa = getMpaById(film.getMpa().getId());
            film.setMpa(fullMpa);
        }
        saveGenres(film.getId(), film.getGenres());
        saveLikes(film.getId(), film.getLikes());

        return film;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public void saveGenres(Long filmId, Set<Genre> genreSet) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbc.update(deleteSql, filmId);

        if (genreSet == null || genreSet.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        System.out.println("Saving genre");
        jdbc.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genreSet.stream().toList().get(i);
                ps.setLong(1, filmId);
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genreSet.size();
            }
        });

    }


    @Override
    public Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbc.query(sql, (rs, rowNum) -> new Genre(
                rs.getLong("genre_id"),
                rs.getString("genre_name")
        ), filmId);
        genres.sort(Comparator.comparing(Genre::getId));
        return new LinkedHashSet<>(genres);
    }


    @Override
    public void saveLikes(Long filmId, Set<Long> userIds) {
        String deleteSql = "DELETE FROM likes WHERE film_id = ?";
        jdbc.update(deleteSql, filmId);

        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        for (Long userId : userIds) {
            jdbc.update(insertSql, filmId, userId);
        }
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    private Mpa getMpaById(Long mpaId) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbc.queryForObject(sql, (rs, rowNum) -> new Mpa(
                rs.getLong("id"),
                rs.getString("name")
        ), mpaId);
    }


}


