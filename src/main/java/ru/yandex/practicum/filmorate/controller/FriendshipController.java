package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendsService;

    @GetMapping
    public List<Friendship> getAllFriendships() {
        return friendsService.getAllFriendships();
    }
}
