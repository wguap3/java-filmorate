package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.likes.mapper.LikesRowMapper;
import ru.yandex.practicum.filmorate.storage.user.mapper.UserRowMapper;

@Configuration
public class MapperConfig {
    @Bean
    public RowMapper<User> userRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public RowMapper<Like> likeRowMapper() {
        return new LikesRowMapper();
    }

}

