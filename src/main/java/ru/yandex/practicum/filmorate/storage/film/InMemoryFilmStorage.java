package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
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
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        data.put(id, film);
        return film;
    }

    @Override
    public Film get(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        return data.get(id);
    }

    @Override
    public void delete(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        data.remove(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Film addLike(long id, long userId) {
        Film film = this.get(id);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film removeLike(long id, long userId) {
        Film film = this.get(id);
        film.getLikes().remove(userId);
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        return data.values().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularFilmSortedByYear(int count, Integer year) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> getPopularFilmSortedByGenre(int count, long genreId) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> getPopularFilmSortedByGenreAndYear(int count, long genreId, Integer year) {
        return new ArrayList<>();
    }

    @Override
    public Set<Long> getLikes(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        return data.get(id).getLikes();
    }

    @Override
    public boolean notContainFilm(long id) {
        return !data.containsKey(id);
    }

    @Override
    public List<Film> getFilmWithName(String name) {
        return null;
    }

    @Override
    public List<Film> getDirectorSortedFilms(Long id, String sortBy) {
        return new ArrayList<>();
    }

}