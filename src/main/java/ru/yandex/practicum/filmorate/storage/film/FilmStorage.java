package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);

    Film getFilmById(Long id);
}
