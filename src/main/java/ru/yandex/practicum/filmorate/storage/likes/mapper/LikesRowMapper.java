package ru.yandex.practicum.filmorate.storage.likes.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikesRowMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        Like like = new Like();
        like.setFilm_id(rs.getLong("film_id"));
        like.setUser_id(rs.getLong("user_id"));
        return like;
    }

}
