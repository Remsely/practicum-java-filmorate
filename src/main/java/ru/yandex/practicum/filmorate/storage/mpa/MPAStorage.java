package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAStorage {
    MPA get(long id);

    List<MPA> get();
}
