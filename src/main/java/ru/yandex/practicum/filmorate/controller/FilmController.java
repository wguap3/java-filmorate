package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        return filmService.create(film);
    }


    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("{filmId}")
    public Optional<Film> getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getTopFilms(count);
    }

}
