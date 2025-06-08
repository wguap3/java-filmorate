package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class LikesService {
    private final LikesStorage likesStorage;

    @Autowired
    public LikesService(@Qualifier("likesDbStorage") LikesStorage likesStorage) {
        this.likesStorage = likesStorage;
    }

    public Collection<Like> getLikes() {
        log.info("Получаем все лайки.");
        return likesStorage.getLikes();
    }

    public Optional<Like> getLikeById(Long likeId) {
        return likesStorage.getLikeById(likeId);
    }

    public Like create(Like like) {
        return likesStorage.create(like);
    }

    public void delete(Long id) {
        likesStorage.delete(id);
    }
}
