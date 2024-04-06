package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre get(long id);

    List<Genre> getAll();

    List<Genre> getFilmGenres(long id);

    List<Genre> addFilmGenres(long id, List<Genre> genres);

    List<Genre> updateFilmGenres(long id, List<Genre> genres);

    void add(long filmId, long genreId);

    void delete(long filmId, long genreId);

    void delete(long filmId);

    boolean notContainGenre(long id);
}