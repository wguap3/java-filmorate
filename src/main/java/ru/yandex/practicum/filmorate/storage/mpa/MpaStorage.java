package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> getMpa();

    Mpa create(Mpa mpa);

    Mpa update(Mpa newMpa);

    void delete(Long id);

    Optional<Mpa> getMpaById(Long id);
}
