package ru.yandex.practicum.filmorate.controller;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController<T> {
    protected Map<Integer, T> data = new HashMap<>();
    protected int currentId = 1;

    public void clear() {
        data.clear();
        currentId = 1;
    }
}
