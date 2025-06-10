package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);

    Optional<Film> getFilmById(Long id);

    void saveGenres(Long filmId, Set<Genre> genres);

    Set<Genre> getGenresByFilmId(Long filmId);

    void saveLikes(Long filmId, Set<Long> userIds);

    Set<Long> getLikesByFilmId(Long filmId);
}
