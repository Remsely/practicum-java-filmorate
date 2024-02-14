package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void addLike(long id, long userId);

    void removeLike(long id, long userId);

    Film get(long id);

    List<Film> getPopular(long count);

    List<Film> getAll();

    void clear();

    boolean containsFilm(long id);
}