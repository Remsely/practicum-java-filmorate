package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;

@Slf4j
@Service
public class MPAService {
    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA getRating(long id) {
        MPA mpa = mpaStorage.get(id);
        log.info("Получен MPA-рейтинг с id {}. MPA: {}", id, mpa);
        return mpa;
    }

    public List<MPA> getAllRatings() {
        List<MPA> mpas = mpaStorage.getAll();
        log.info("Получен список всех MPA-рейтингов. List<MPA>: {}", mpas);
        return mpas;
    }
}