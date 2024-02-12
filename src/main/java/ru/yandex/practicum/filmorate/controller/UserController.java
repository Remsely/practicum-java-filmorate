package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User> {
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к /users. Тело запроса: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(currentId++);
        data.put(user.getId(), user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к /users. Тело запроса: {}", user);
        if (data.containsKey(user.getId())) {
            data.put(user.getId(), user);
            return ResponseEntity.ok(user);
        }
        log.warn("Пользователь с id {} не найден.", user.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        log.info("Получен GET-запрос к /users.");
        return ResponseEntity.ok(new ArrayList<>(data.values()));
    }
}