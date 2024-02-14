package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    // На каком уровне стоит выбрасывать исключения? Я решил, что лучше выкидывать исключения в бизнес логике, чтобы
    // storage выполнял исключительно функционал хранения. Но, наверное, все же стоит добавить проверки и в методы
    // storage, чтобы storage был проще в использовании, но не выбрасывать там исключения, а возвращать пустого
    // пользователя, к примеру?
    public User updateUser(User user) {
        if (userStorage.containsUser(user.getId())) {
            return userStorage.update(user);
        }
        throw new EntityNotFoundException(
                new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", user.getId()))
        );
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addFriend(long id, long friendId) {
        if (!userStorage.containsUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (!userStorage.containsUser(friendId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", friendId))
            );
        }
        return userStorage.addFriend(id, friendId);
    }

    public User getFriend(long id) {
        if (!userStorage.containsUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        return userStorage.get(id);
    }

    public User removeFriend(long id, long friendId) {
        if (!userStorage.containsUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (!userStorage.containsUser(friendId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", friendId))
            );
        }
        return userStorage.removeFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        if (userStorage.containsUser(id)) {
            return userStorage.getFriends(id);
        }
        throw new EntityNotFoundException(
                new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
        );
    }

    public List<User> getCommonFriends(long id, long otherId) {
        if (!userStorage.containsUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (!userStorage.containsUser(otherId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", otherId))
            );
        }
        return userStorage.getCommonFriends(id, otherId);
    }

    public void clearStorage() {
        userStorage.clear();
    }
}