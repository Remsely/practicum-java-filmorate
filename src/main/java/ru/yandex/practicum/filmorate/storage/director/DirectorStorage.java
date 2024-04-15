package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(long id);

    Director add(Director director);

    Director update(Director director);

    List<Director> getAll();

    List<Director> addDirectors(long id, List<Director> directors);

    List<Director> getFilmDirectors(long id);

    void delete(long id);

    void deleteFilmDirectors(long id);

    void updateFilmDirectors(long id, List<Director> directors);

    boolean notContainDirector(long id);

}
