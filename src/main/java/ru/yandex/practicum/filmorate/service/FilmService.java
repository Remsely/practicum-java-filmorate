package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
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

    public void deleteFilm(long id) {
        filmStorage.delete(id);
        log.info("Фильм удален id: {}", id);
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAll();
        log.info("Получен список всех фильмов. List<Film>: {}", films);
        return films;
    }

    public Film addLike(long id, long userId) {
        checkUserExist(userId);

        Film film = filmStorage.addLike(id, userId);
        log.info("Добавлен лайк фильму с id {} от пользователя с id {}. Film: {}", id, userId, film);

        FeedEventType eventType = FeedEventType.LIKE;
        FeedOperation operation = FeedOperation.ADD;

        feedStorage.add(userId, id, eventType, operation);
        log.debug(
                "Лайк пользователя с id {} фильму с id {} добавлен в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                userId, id, userId, id, eventType, operation
        );
        return film;
    }

    public Film removeLike(long id, long userId) {
        checkUserExist(userId);

        Film film = filmStorage.removeLike(id, userId);
        log.info("Удален лайк фильму с id {} от пользователя с id {}. Film: {}", id, userId, film);

        FeedEventType eventType = FeedEventType.LIKE;
        FeedOperation operation = FeedOperation.REMOVE;

        feedStorage.add(userId, id, eventType, operation);
        log.debug(
                "Удаление лайка фильму с id {} пользователя с id {} добавлено в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                id, userId, userId, id, eventType, operation
        );
        return film;
    }

    public List<Film> getCommonFilm(long id1, long id2) {
        checkUserExist(id1);
        checkUserExist(id2);

        List<Film> commonFilms = filmStorage.getCommonFilms(id1, id2);
        log.info("Получен список общих фильмов пользователей с id {} и {}. List<Film>: {}", id1, id2, commonFilms);
        return commonFilms;
    }

    public List<Film> getDirectorFilmsList(long id, String sortBy) {
        List<Film> films = filmStorage.getDirectorSortedFilms(id, sortBy);
        log.info("Получен список фильмов режисера Director Id: {}, " +
                "сортировка sortBy: {} list: {}", id, sortBy, films);
        return films;
    }

    public List<Film> search(String query, List<String> by) {
        List<Film> films = filmStorage.search(query, by);
        log.info("Получен список фильмов по строке поиска {} по критериям {}", query, by);
        return films;
    }

    public List<Film> getPopularFilm(int count, Long id, Integer year) {
        List<Film> films = filmStorage.getPopularFilm(count, id, year);
        log.info("Получен список {} самых популярных фильмов с genre_id = {} и release = {}" +
                " List<Film>: {} ", count, id, year, films);
        return films;
    }

    private void logQueryInfo(String query, List<String> by, List<Film> films) {
        log.info("Получен список фильмов по запросу '{}'. Поиск по {}: list: {}", query, by, films);
    }

    private void checkUserExist(long id) {
        if (userStorage.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
    }
}