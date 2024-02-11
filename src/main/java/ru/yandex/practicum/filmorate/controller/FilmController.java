package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    //Здесь я использовал ResponseEntity, т. к. при выбросе исключения возникает ERROR, и программа падает на тестах.
    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к /films. Тело запроса: {}", film);
        film.setId(currentId++);
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к /films. Тело запроса: {}", film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return ResponseEntity.ok(film);
        }
        log.warn("Пользователь с id {} не найден.", film.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        log.info("Получен GET-запрос к /films.");
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    public void clear() {
        currentId = 1;
        films.clear();
    }
}