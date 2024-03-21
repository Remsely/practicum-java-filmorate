package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к /users. Тело запроса: {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к /users. Тело запроса: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Получен GET-запрос к /users/{}.", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен GET-запрос к /users.");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{followerId}")
    public User putFriend(@PathVariable long id, @PathVariable long followerId) {
        log.info("Получен PUT-запрос к /users/{}/friends/{}. {} ", id, followerId, userService.getUser(id));
        return userService.addFriend(id, followerId);
    }

    @DeleteMapping("/{id}/friends/{followerId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long followerId) {
        log.info("Получен DELETE-запрос к /users/{}/friends/{}.", id, followerId);
        return userService.removeFriend(id, followerId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Получен GET-запрос к /users/{}/friends.", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Получен GET-запрос к /users/{}/friends/common/{}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}