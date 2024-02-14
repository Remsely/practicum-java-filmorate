package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);
    Film update(Film film);
    void addLike(int id, int userId);
    void removeLike(int id, int userId);
    Film get(int id);
    List<Film> getPopular(int count);
    List<Film> getAll();
    void clear();
}
