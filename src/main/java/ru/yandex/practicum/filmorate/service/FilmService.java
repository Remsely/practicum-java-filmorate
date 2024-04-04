package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        Film savedFilm = filmStorage.add(film);
        log.info("Фильм добавлен. Film: {}", savedFilm);
        return savedFilm;
    }

    public Film updateFilm(Film film) {
        Film savedUFilm = filmStorage.update(film);
        log.info("Данные фильма обновлены. Film: {}", savedUFilm);
        return savedUFilm;
    }

    public Film getFilm(long id) {
        Film film = filmStorage.get(id);
        log.info("Получен фильм с id {}. Film: {}", id, film);
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAll();
        log.info("Получен список всех фильмов. List<Film>: {}", films);
        return films;
    }

    public Film addLike(long id, long userId) {
        if (userStorage.notContainUser(userId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        Film film = filmStorage.addLike(id, userId);
        log.info("Добавлен лайк фильму с id {} от пользователя с id {}. Film: {}", id, userId, film);
        return film;
    }

    public Film removeLike(long id, long userId) {
        if (userStorage.notContainUser(userId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        Film film = filmStorage.removeLike(id, userId);
        log.info("Удален лайк фильму с id {} от пользователя с id {}. Film: {}", id, userId, film);
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> films = filmStorage.getPopular(count);
        log.info("Получен список {} самых популярных фильмов. List<Film>: {}", count, films);
        return films;
    }

    // DIRECTOR.Получить список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    public List<Film> getDirectorFilmsList(long id, String sortBy) {
        List<Film> films = filmStorage.getDirectorSortedFilms(id, sortBy);
        log.info("Получен список фильмов {} режиссера Director id {} отсортированных по количеству {}", films, id, sortBy);
        return films;
    }
}