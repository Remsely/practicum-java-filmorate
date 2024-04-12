package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film get(long id);

    Film addLike(long id, long userId);

    Film removeLike(long id, long userId);

    List<Film> getAll();

    List<Film> getPopular(int count);

    List<Film> getPopularFilmSortedByYear(int count, Integer year);

    List<Film> getPopularFilmSortedByGenre(int count, long genreId);

    List<Film> getPopularFilmSortedByGenreAndYear(int count, long genreId, Integer year);

    List<Film> getFilmWithName(String name);

    List<Film> getDirectorSortedFilms(Long id, String sortBy);

    Set<Long> getLikes(long id);

    void delete(long id);

    boolean notContainFilm(long id);
}
