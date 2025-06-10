package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    public Long filmId;
    public Long userId;
}
