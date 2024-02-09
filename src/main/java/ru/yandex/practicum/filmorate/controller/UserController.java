package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к /users. Тело запроса: {}", user);
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к /users. Тело запроса: {}", user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        log.warn("Пользователь с id {} не найден.", user.getId());
        throw new RuntimeException("Пользователь с id " + user.getId() + "не найден.");
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен GET-запрос к /users.");
        return new ArrayList<>(users.values());
    }
}