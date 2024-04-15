package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director getDirector(long id) {
        Director director = directorStorage.get(id);
        log.info("Получен режиссер с id {}. Director: {}", id, director);
        return director;
    }

    public Director addDirector(Director director) {
        Director savedDirector = directorStorage.add(director);
        log.info("Добавлен режиссер Director: {}", savedDirector);
        return savedDirector;
    }

    public Director updateDirector(Director director) {
        Director savedDirector = directorStorage.update(director);
        log.info("Обновлен режиссер Director: {}", savedDirector);
        return savedDirector;
    }

    public void deleteDirector(long id) {
        directorStorage.delete(id);
        log.info("Удален режиссер directorId: {}", id);
    }

    public List<Director> getAllDirectors() {
        List<Director> directors = directorStorage.getAll();
        log.info("Получен список всех режиссеров List<Director>: {}", directors);
        return directors;
    }
}
