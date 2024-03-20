package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User get(long id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User addFriend(long id, long friendId) {
        return null;
    }

    @Override
    public User removeFriend(long id, long friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(long id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        return null;
    }

    @Override
    public boolean notContainUser(long id) {
        return false;
    }
}
