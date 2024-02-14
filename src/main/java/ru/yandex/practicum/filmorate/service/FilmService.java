package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.containsFilm(film.getId())) {
            return filmStorage.update(film);
        }
        throw new EntityNotFoundException(
                new ErrorResponse("Film id", "Не найден фильм с таким ID.")
        );
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void clearStorage() {
        filmStorage.clear();
    }
}