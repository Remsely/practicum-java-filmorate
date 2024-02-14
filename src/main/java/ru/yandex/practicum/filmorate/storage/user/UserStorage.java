package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User addFriend(long id, long friendId);

    User removeFriend(long id, long friendId);

    User update(User user);

    User get(long id);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

    List<User> getAll();

    void clear();

    boolean containsUser(long id);
}