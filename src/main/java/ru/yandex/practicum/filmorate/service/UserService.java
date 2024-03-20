package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User getUser(long id) {
        return userStorage.get(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addFriend(long id, long friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public User removeFriend(long id, long friendId) {
        return userStorage.removeFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }
}