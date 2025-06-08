package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String DELETE_BY_ID_QUERY = "DELETE  FROM mpa WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT id, name FROM mpa WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO mpa (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE mpa SET name = ? WHERE id = ?";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getMpaById(Long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    @Override
    public Mpa create(Mpa mpa) {
        long id = insert(
                INSERT_QUERY,
                mpa.getName()
        );
        return mpa;
    }

    @Override
    public Mpa update(Mpa mpa) {
        update(
                UPDATE_QUERY,
                mpa.getName(),
                mpa.getId()
        );
        return mpa;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }
}
