package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director get(long id); // Получить режиссера

    Director add(Director director); // Добавить режиссера

    Director update(Director director); // Обновить режиссера

    void delete(long id); // Удалить режиссера

    List<Director> getDirectorsWithName(String name);

    void deleteFilmDirectors(long id); // Удалить связь фильма с режиссером

    List<Director> getAll(); // Получить список всех режиссеров

    boolean notContainDirector(long id); // Проверить есть ли режиссер в БД

    List<Director> addDirectors(long id, List<Director> directors); // Добавить режиссера к фильму

    List<Director> getFilmDirectors(long id); // Получить режиссера у фильма

    boolean filmNotContainDirector(long filmId, long directorId); // Проверить содержится ли режиссер у фильма
}
