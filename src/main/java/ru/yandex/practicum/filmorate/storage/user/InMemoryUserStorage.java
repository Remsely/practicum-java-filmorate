package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
    public User addFriend(long id, long friendId) {
        User user = data.get(id);
        user.getFriends().add(friendId);
        return user;
    }

    @Override
    public User removeFriend(long id, long friendId) {
        User user = data.get(id);
        user.getFriends().remove(friendId);
        return user;
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        data.put(id, user);
        return user;
    }

    @Override
    public User get(long id) {
        return data.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        return data.get(id).getFriends().stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> commonFriends = data.get(id).getFriends();
        Set<Long> otherUserFriends = data.get(otherId).getFriends();
        commonFriends.retainAll(otherUserFriends);
        return commonFriends.stream()
                .map(data::get)
                .collect(Collectors.toList());
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

    @Override
    public boolean containsUser(long id) {
        return data.containsKey(id);
    }
}
