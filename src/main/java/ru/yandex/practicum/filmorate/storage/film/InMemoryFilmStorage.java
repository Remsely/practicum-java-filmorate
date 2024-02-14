package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> data;
    private long currentId;

    public InMemoryFilmStorage() {
        data = new HashMap<>();
        currentId = 1;
    }

    @Override
    public Film add(Film film) {
        film.setId(currentId++);
        data.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (data.containsKey(id)) {
            data.put(id, film);
            return film;
        }
        throw new NotFoundException("Film with such id is not found.");
    }

    @Override
    public void addLike(int id, int userId) {

    }

    @Override
    public void removeLike(int id, int userId) {

    }

    @Override
    public Film get(int id) {
        return null;
    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void clear() {
        currentId = 1;
        data.clear();
    }
}
