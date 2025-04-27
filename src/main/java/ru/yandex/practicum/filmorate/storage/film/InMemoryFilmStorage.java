package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATA_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан: {}", film);
        return film;
    }

    @Override
    public Film update(@Valid @RequestBody Film newFilm) {
        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            throw new NotFoundException("Фильм не найден!");
        }

        if (newFilm.getName() != null) {
            existingFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            existingFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getDuration() != null) {
            existingFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        log.info("Фильм обновлён: {}", existingFilm);
        return existingFilm;
    }

    @Override
    public void delete(Long id) {
        if (films.containsKey(id)) {
            log.info("Фильм удален");
            films.remove(id);
        } else {
            throw new NotFoundException("Фильм с " + id + "не найден.");
        }
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Поиск фильма по id: {}", id);
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        return film;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
