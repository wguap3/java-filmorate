package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Optional;

public interface LikesStorage {
    Collection<Like> getLikes();

    Like create(Like like);


    void delete(Long id);

    Optional<Like> getLikeById(Long id);
}
