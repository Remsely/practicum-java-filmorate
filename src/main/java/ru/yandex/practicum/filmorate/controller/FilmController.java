package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к /films. Тело запроса: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к /films. Тело запроса: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.info("Получен GET-запрос к /films/{}.", id);
        return filmService.getFilm(id);
    }

    @DeleteMapping(value = "/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.info("Получен DELETE-запрос к /films/{}.", filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен GET-запрос к /films.");
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен PUT-запрос к /films/{}/like/{}.", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен DELETE-запрос к /films/{}/like/{}.", id, userId);
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                 @RequestParam(defaultValue = "0") long genreId,
                                 @RequestParam(defaultValue = "0") Integer year) {

        log.info("Получен GET-запрос к /popular/count={}/genreId={}/year={}.", count, genreId, year);
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") long userId,
                                     @RequestParam(value = "friendId") long friendId) {
        log.info("Получен GET-запрос к /films/common?userId={}&friendId={}.", userId, friendId);
        return filmService.getCommonFilm(userId, userId);
    }

    @GetMapping("/director/{id}")
    public List<Film> getDirectorFilms(@PathVariable long id,
                                       @RequestParam(defaultValue = "likes") String sortBy) {
        log.info("Получен GET-запрос к films/director/{directorId}?sortBy={}", sortBy);
        return filmService.getDirectorFilmsList(id, sortBy);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam(name = "query", required = true) String query, @RequestParam(name = "by", defaultValue = "title") List<String> by) {
        log.info("Получен GET-запрос к /films/search?query=" + query + "&by=" + by);
        return filmService.search(query, by);
    }
}
