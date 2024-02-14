package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

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
        data.put(id, film);
        return film;
    }

    @Override
    public void addLike(long id, long userId) {
        data.get(id).getLikes().add(userId);
    }

    @Override
    public void removeLike(long id, long userId) {
        data.get(id).getLikes().remove(userId);
    }

    @Override
    public Film get(long id) {
        return data.get(id);
    }

    @Override
    public List<Film> getPopular(long count) {
        return data.values().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
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

    @Override
    public boolean containsFilm(long id) {
        return data.containsKey(id);
    }
}