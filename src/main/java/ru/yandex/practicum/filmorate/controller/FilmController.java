package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 11, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к /films. Тело запроса: {}", film);
        if (isNotCorrectDate(film.getReleaseDate())) {
            throw new ValidationException("The release date of the film cannot be earlier than 28.12.1895.");
        }
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateUser(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к /films. Тело запроса: {}", film);
        if (films.containsKey(film.getId())) {
            if (isNotCorrectDate(film.getReleaseDate())) {
                throw new ValidationException("The release date of the film cannot be earlier than 28.12.1895.");
            }
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

    private boolean isNotCorrectDate(LocalDate releaseDate) {
        return releaseDate.isBefore(MIN_FILM_DATE);
    }
}