package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private Long id;
    private Long firstUserId;
    private Long secondUserId;
    private FriendshipStatus status;

    public enum FriendshipStatus {
        UNCONFIRMED,
        CONFIRMED
    }
}
