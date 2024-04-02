package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(long id);

    Director add(Director director);

    Director update(Director director);

    void delete(long id);

    List<Director> getAll();

    boolean notContainDirector(long id);

    List<Director> addDirectors(long id, List<Director> directors);

    List<Director> getFilmDirectors(long id);
}
