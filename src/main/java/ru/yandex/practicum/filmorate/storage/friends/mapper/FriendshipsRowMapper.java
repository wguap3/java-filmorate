package ru.yandex.practicum.filmorate.storage.friends.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipsRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship fs = new Friendship();
        fs.setId(rs.getLong("friendship_id"));
        fs.setFirstUserId(rs.getLong("first_user_id"));
        fs.setSecondUserId(rs.getLong("second_user_id"));
        fs.setStatus(Friendship.FriendshipStatus.valueOf(rs.getString("status")));
        return fs;
    }
}
