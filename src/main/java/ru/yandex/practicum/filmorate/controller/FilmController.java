package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к /films. Тело запроса: {}", film);
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateUser(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к /films. Тело запроса: {}", film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        log.warn("Пользователь с id {} не найден.", film.getId());
        throw new RuntimeException("Фильм с таким id не найден.");
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен GET-запрос к /films.");
        return new ArrayList<>(films.values());
    }
}