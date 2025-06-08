package ru.yandex.practicum.filmorate.storage.mpa.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long mpaId = rs.getLong("id");
        String mpaName = rs.getString("name");
        Mpa mpa = new Mpa(mpaId, mpaName);
        return mpa;
    }
}