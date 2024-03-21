package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sqlQuery = "insert into users (name, login, email, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
                    statement.setString(1, user.getName());
                    statement.setString(2, user.getLogin());
                    statement.setString(3, user.getEmail());
                    statement.setDate(4, Date.valueOf(user.getBirthday()));
                    return statement;
                },
                keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return this.get(id);
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        String sqlQuery = "update USERS set name = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                id);
        return this.get(user.getId());
    }

    @Override
    public User get(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        String filmSqlQuery = "select * from USERS where USER_ID = ?";
        return jdbcTemplate.queryForObject(filmSqlQuery, this::mapRowToUser, id);
    }


    @Override
    public List<User> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addFriend(long id, long followerId) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (this.notContainUser(followerId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", followerId))
            );
        }
        if (containsFriendApproval(followerId, id, false)) {
            String sqlQuery = "update follows set approved = ? where target_id = ? and follower_id = ?";
            jdbcTemplate.update(sqlQuery, true, followerId, id);
        } else {
            String sqlQuery = "insert into follows (target_id, follower_id, approved) values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, followerId, false);
        }
        return this.get(id);
    }


    @Override
    public User removeFriend(long id, long followerId) {
        if (this.notContainUser(followerId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", followerId))
            );
        }
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (containsFriendApproval(followerId, id, true)) {
            String sqlQuery = "delete from follows where target_id = ? and follower_id = ?";
            jdbcTemplate.update(sqlQuery, followerId, id);
            this.addFriend(id, followerId);
        } else if (containsFriendApproval(id, followerId, true)) {
            String sqlQuery = "update follows set approved = ? where target_id = ? and follower_id = ?";
            jdbcTemplate.update(sqlQuery, false, id, followerId);
        } else if (containsFriendApproval(id, followerId, false)) {
            String sqlQuery = "delete from follows where target_id = ? and follower_id = ?";
            jdbcTemplate.update(sqlQuery, id, followerId);
        }
        return this.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        String sqlQuery = "select * " +
                "from USERS " +
                "where USER_ID in ( " +
                "select target_id " +
                "from FOLLOWS " +
                "where FOLLOWER_ID = ? and APPROVED = true " +
                ") " +
                "or USER_ID in ( " +
                "select follower_id " +
                "from FOLLOWS " +
                "where target_id = ? " +
                ")";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        if (this.notContainUser(otherId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", otherId))
            );
        }
        Set<User> commonFriends = new HashSet<>(this.getFriends(id));
        commonFriends.retainAll(this.getFriends(otherId));
        return new ArrayList<>(commonFriends);
    }

    @Override
    public boolean notContainUser(long id) {
        String sqlQuery = "select count(*) from USERS where USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    private Set<Long> getFriendsIds(long id) {
        String sqlQuery = "select USER_ID " +
                "from USERS " +
                "where USER_ID in ( " +
                "select target_id " +
                "from FOLLOWS " +
                "where FOLLOWER_ID = ? and APPROVED = true " +
                ") " +
                "or USER_ID in ( " +
                "select follower_id " +
                "from FOLLOWS " +
                "where target_id = ? " +
                ")";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> rs.getLong("user_id"),
                id, id));
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        return User.builder()
                .id(id)
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(this.getFriendsIds(id))
                .build();
    }

    private boolean containsFriendApproval(long targetId, long friendId, boolean approved) {
        String sqlQuery = "select count(*) from follows where target_id = ? and follower_id = ? and approved = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, targetId, friendId, approved);
        return count != null && count == 1;
    }
}
