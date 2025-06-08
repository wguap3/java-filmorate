package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    public Long film_id;
    public Long user_id;
}
