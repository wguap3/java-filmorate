package ru.yandex.practicum.filmorate.storage.genre.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long genreId = rs.getLong("genre_id");
        String genreName = rs.getString("genre_name");
        Genre genre = new Genre(genreId, genreName);
        return genre;
    }

}
