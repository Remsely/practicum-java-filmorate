package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.List;

@Service
public class MPAService {
    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA get(long id) {
        return mpaStorage.get(id);
    }

    public List<MPA> getAll() {
        return mpaStorage.getAll();
    }
}
