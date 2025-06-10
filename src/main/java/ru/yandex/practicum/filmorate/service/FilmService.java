package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getFilms();
    }

    public Optional<Film> getFilmById(Long id) {
        log.info("Получение фильма по id: {}", id);
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film.getName());
        Film createdFilm = filmStorage.create(film);
        filmStorage.saveGenres(createdFilm.getId(), createdFilm.getGenres());
        filmStorage.saveLikes(createdFilm.getId(), createdFilm.getLikes());
        return createdFilm;
    }

    public Film update(Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        Film updatedFilm = filmStorage.update(film);
        filmStorage.saveGenres(updatedFilm.getId(), updatedFilm.getGenres());
        filmStorage.saveLikes(updatedFilm.getId(), updatedFilm.getLikes());
        return updatedFilm;
    }

    public void delete(Long id) {
        log.info("Удаление фильма с id: {}", id);
        filmStorage.delete(id);
    }

    public Film addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        film.getLikes().add(userId);
        filmStorage.saveLikes(filmId, film.getLikes());
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка у фильма {} от пользователя {}", filmId, userId);
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        film.getLikes().remove(userId);
        filmStorage.saveLikes(filmId, film.getLikes());
        return film;
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Получение топ {} фильмов по лайкам", count);
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Set<Genre> getGenresByFilmId(Long filmId) {
        log.info("Получение жанров для фильма с id: {}", filmId);
        return filmStorage.getGenresByFilmId(filmId);
    }
}
