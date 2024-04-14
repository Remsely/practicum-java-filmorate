package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.FeedEntity;

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
    public User update(User user) {
        long id = user.getId();

        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        data.put(id, user);
        return user;
    }

    @Override
    public User get(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        return data.get(id);
    }

    // Добавлен так как есть в интерфейсе "UserStorage"
    @Override
    public void delete(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        data.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User addFriend(long id, long friendId) {
        User user = this.get(id);
        user.getFriends().add(friendId);
        return user;
    }

    @Override
    public User removeFriend(long id, long friendId) {
        User user = this.get(id);
        user.getFriends().remove(friendId);
        return user;
    }

    @Override
    public List<User> getFriends(long id) {
        User user = this.get(id);
        return user.getFriends().stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        User user = this.get(id);
        User otherUser = this.get(otherId);

        Set<Long> commonFriends = new TreeSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends.stream()
                .map(data::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getLikes(long id) {
        return Collections.emptyList();
    }

    @Override
    public List<FeedEntity> getFeed(long id) {
        return Collections.emptyList();
    }

    @Override
    public boolean notContainUser(long id) {
        return !data.containsKey(id);
    }

}