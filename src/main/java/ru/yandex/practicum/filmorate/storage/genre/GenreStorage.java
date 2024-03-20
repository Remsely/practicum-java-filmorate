package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre get(long id);

    List<Genre> get();

    List<Genre> getFilmGenres(long id);

    List<Genre> addFilmGenres(long id, List<Genre> genres);
}
