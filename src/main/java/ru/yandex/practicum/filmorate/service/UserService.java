package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.FeedEntity;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
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
        User savedUser = userStorage.add(user);
        log.info("Пользователь добавлен. User: {}", savedUser);
        return savedUser;
    }

    public User updateUser(User user) {
        User savedUser = userStorage.update(user);
        log.info("Данные пользователя обновлены. User: {}", savedUser);
        return savedUser;
    }

    public User getUser(long id) {
        User user = userStorage.get(id);
        log.info("Получен пользователь с id {}. User: {}", id, user);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAll();
        log.info("Получен список всех пользователей. List<User>: {}", users);
        return users;
    }

    public User addFriend(long id, long followerId) {
        User user = userStorage.addFriend(id, followerId);
        log.info("Сохранена заявка на добавление в друзья пользователю с id {} от пользователя с id {}. " +
                        "User (id: {}): {}",
                id, followerId, id, user);
        return user;
    }

    public User removeFriend(long id, long followerId) {
        User user = userStorage.removeFriend(id, followerId);
        log.info("Удалена заявка на добавление в друзья пользователю с id {} от пользователя с id {}. " +
                        "User (id: {}): {}",
                id, followerId, id, user);
        return user;
    }

    public List<User> getFriends(long id) {
        List<User> friends = userStorage.getFriends(id);
        log.info("Получен список всех друзей пользователя с id {}. List<User>: {}", id, friends);
        return friends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> friends = userStorage.getCommonFriends(id, otherId);
        log.info("Получен список общих друзей пользователей с id {} и {}. List<User>: {}", id, otherId, friends);
        return friends;
    }

    public List<FeedEntity> getUserFeed(long userId) {
        List<FeedEntity> feed = userStorage.getFeed(userId);
        log.info("Получен список последних событий на платформе для пользователя с id {}. List<FeedEntity>: {}",
                userId, feed);
        return userStorage.getFeed(userId);
    }
}