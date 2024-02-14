package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    void addFriend(int id, int friendId);

    void removeFriend(int id, int friendId);

    User update(User user);

    User get(int id);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    List<User> getAll();

    void clear();
}
