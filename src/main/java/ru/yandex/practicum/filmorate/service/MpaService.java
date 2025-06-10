package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> getMpa() {
        log.info("Получаем список всех mpa.");
        return mpaStorage.getMpa();
    }

    public Mpa getMpaById(Long id) {
        log.info("Получаем mpa по id: {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Mpa с id " + id + " не найден."));
    }

    public Mpa create(Mpa mpa) {
        log.info("Создаем mpa.");
        return mpaStorage.create(mpa);
    }

    public Mpa update(Mpa mpa) {
        log.info("Обновляем mpa");
        return mpaStorage.update(mpa);
    }

    public void delete(Long id) {
        log.info("Удаляем mpa");
        mpaStorage.delete(id);
    }
}
