package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> data;
    private long currentId;

    public InMemoryUserStorage() {
        data = new HashMap<>();
        currentId = 1;
    }

    @Override
    public User add(User user) {
        user.setId(currentId++);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {

    }

    @Override
    public void removeFriend(int id, int friendId) {

    }

    @Override
    public User update(User user) {
        long id = user.getId();
        if (data.containsKey(id)) {
            data.put(id, user);
            return user;
        }
        throw new NotFoundException("User with such id is not found.");
    }

    @Override
    public User get(int id) {
        return null;
    }

    @Override
    public List<User> getFriends(int id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void clear() {
        currentId = 1;
        data.clear();
    }
}
