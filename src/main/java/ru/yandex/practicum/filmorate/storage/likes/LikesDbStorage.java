package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("likesDbStorage")
public class LikesDbStorage extends BaseStorage<Like> implements LikesStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM likes";
    private static final String DELETE_BY_ID_QUERY = "DELETE  FROM likes WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes (film_id,user_id) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE likes SET film_id = ?, user_id = ?";

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Like> getLikes() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Like> getLikeById(Long likeId) {
        return findOne(FIND_BY_ID_QUERY, likeId);
    }

    @Override
    public Like create(Like like) {
        jdbc.update(INSERT_QUERY, like.getFilm_id(), like.getUser_id());
        return like;
    }


    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }
}
