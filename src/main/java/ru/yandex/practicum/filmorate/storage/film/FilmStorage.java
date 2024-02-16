package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film get(long id);

    List<Film> getAll();

    Film addLike(long id, long userId);

    Film removeLike(long id, long userId);

    List<Film> getPopular(int count);

    void clear();

    boolean notContainFilm(long id);
}