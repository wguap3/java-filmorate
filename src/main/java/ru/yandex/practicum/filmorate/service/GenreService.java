package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> getGenre() {
        log.info("Получаем список всех жанров.");
        return genreStorage.getGenre();
    }

    public Genre getGenreById(Long id) {
        log.info("Получаем жанр по id: {}", id);
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден."));
    }

    public Genre create(Genre genre) {
        log.info("Создаем жанр.");
        return genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        log.info("Обновляем жанр");
        return genreStorage.update(genre);
    }

    public void delete(Long id) {
        log.info("Удаляем жанр");
        genreStorage.delete(id);
    }
}
