package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        log.info("Добавляем лайк фильму " + filmId + "от пользователя " + userId + ".");
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        filmStorage.update(film);

        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        log.info("Удаляем  лайк  у фильма " + filmId + "от пользователя " + userId + ".");
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Возвращаем топ фильмов.");
        if (count == null) {
            count = 10;
        }
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
