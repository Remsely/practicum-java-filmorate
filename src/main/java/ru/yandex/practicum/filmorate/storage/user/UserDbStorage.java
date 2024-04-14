package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.FeedEntity;
import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO user_data (name, login, email, birthday) VALUES (?, ?, ?, ?);";

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
        checkUserExist(id);

        String sqlQuery = "UPDATE user_data SET name = ?, login = ?, email = ?, birthday = ? WHERE user_id = ?";
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
        checkUserExist(id);
        String filmSqlQuery = "SELECT * FROM user_data WHERE user_id = ?";
        return jdbcTemplate.queryForObject(filmSqlQuery, this::mapRowToUser, id);
    }

    public void delete(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
        String sqlQuery = "DELETE FROM user_data WHERE user_id = " + id;
        jdbcTemplate.execute(sqlQuery);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM user_data";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addFriend(long id, long followerId) {
        checkUserExist(id);
        checkUserExist(followerId);

        if (containsFriendApproval(followerId, id, false)) {
            String sqlQuery = "UPDATE follow SET approved = ? WHERE target_id = ? AND follower_id = ?";
            jdbcTemplate.update(sqlQuery, true, followerId, id);
        } else {
            String sqlQuery = "INSERT INTO follow (target_id, follower_id, approved) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, id, followerId, false);
        }
        return this.get(id);
    }


    @Override
    public User removeFriend(long id, long followerId) {

        checkUserExist(followerId);
        checkUserExist(id);

        if (containsFriendApproval(followerId, id, true)) {
            String sqlQuery = "DELETE FROM follow WHERE target_id = ? AND follower_id = ?";
            jdbcTemplate.update(sqlQuery, followerId, id);
            this.addFriend(id, followerId);
        } else if (containsFriendApproval(id, followerId, true)) {
            String sqlQuery = "UPDATE follow SET approved = ? WHERE target_id = ? AND follower_id = ?";
            jdbcTemplate.update(sqlQuery, false, id, followerId);
        } else if (containsFriendApproval(id, followerId, false)) {
            String sqlQuery = "DELETE FROM follow WHERE target_id = ? AND follower_id = ?";
            jdbcTemplate.update(sqlQuery, id, followerId);
        }
        return this.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        checkUserExist(id);

        String sqlQuery =
                "SELECT * " +
                        "FROM user_data " +
                        "WHERE user_id IN ( " +
                        "    SELECT target_id " +
                        "    FROM follow " +
                        "    WHERE follower_id = ? " +
                        "      AND approved = true " +
                        ") OR user_id IN ( " +
                        "    SELECT follower_id " +
                        "    FROM follow " +
                        "    WHERE target_id = ? " +
                        ")";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        checkUserExist(id);
        checkUserExist(otherId);

        String sqlQuery =
                "SELECT u1.user_id, u1.email, u1.login, u1.birthday, u1.name " +
                        "FROM ( " +
                        "    SELECT * " +
                        "    FROM user_data " +
                        "    WHERE user_id IN ( " +
                        "        SELECT target_id " +
                        "        FROM follow " +
                        "        WHERE follower_id = ? " +
                        "          AND approved = TRUE " +
                        "    ) OR user_id IN ( " +
                        "        SELECT follower_id " +
                        "        FROM follow " +
                        "        WHERE target_id = ? " +
                        "    ) " +
                        ") AS u1 " +
                        "INNER JOIN ( " +
                        "    SELECT * " +
                        "    FROM user_data " +
                        "    WHERE user_id IN ( " +
                        "        SELECT target_id " +
                        "        FROM follow " +
                        "        WHERE follower_id = ? " +
                        "          AND approved = TRUE " +
                        "    ) OR user_id IN ( " +
                        "        SELECT follower_id " +
                        "        FROM follow " +
                        "        WHERE target_id = ? " +
                        "    ) " +
                        ") AS u2 ON u1.user_id = u2.user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id, otherId, otherId);
    }

    @Override
    public List<Long> getLikes(long id) {
        String sql = "select * from like_film where user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"), id);
    }

    @Override
    public List<FeedEntity> getFeed(long id) {
        checkUserExist(id);
        String sqlQuery =
                "SELECT  " +
                        "    f.event_id, " +
                        "    f.user_id, " +
                        "    f.entity_id, " +
                        "    f.time, " +
                        "    eo.name AS operation_name, " +
                        "    et.name AS type_name " +
                        "FROM  " +
                        "    feed f " +
                        "JOIN  " +
                        "    event_operation eo ON f.operation_id = eo.operation_id " +
                        "JOIN  " +
                        "    event_type et ON f.type_id = et.type_id " +
                        "WHERE  " +
                        "    f.user_id = ? " +
                        "ORDER BY " +
                        "    f.time";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeedEntity, id);
    }

    @Override
    public boolean notContainUser(long id) {
        String sqlQuery = "select count(*) from user_data where USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    private Set<Long> getFriendsIds(long id) {
        String sqlQuery =
                "SELECT user_id " +
                        "FROM user_data " +
                        "WHERE user_id IN ( " +
                        "    SELECT target_id " +
                        "    FROM follow " +
                        "    WHERE follower_id = ? " +
                        "      AND approved = TRUE " +
                        ") OR user_id IN ( " +
                        "    SELECT follower_id " +
                        "    FROM follow " +
                        "    WHERE target_id = ? " +
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

    private FeedEntity mapRowToFeedEntity(ResultSet rs, int rowNum) throws SQLException {
        return FeedEntity.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .operation(FeedOperation.valueOf(rs.getString("operation_name")))
                .eventType(FeedEventType.valueOf(rs.getString("type_name")))
                .timestamp(rs.getDate("time").getTime())
                .build();
    }

    private boolean containsFriendApproval(long targetId, long friendId, boolean approved) {
        String sqlQuery = "SELECT COUNT(*) FROM follow WHERE target_id = ? AND follower_id = ? AND approved = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, targetId, friendId, approved);
        return count != null && count == 1;
    }

    private void checkUserExist(long id) {
        if (this.notContainUser(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", id))
            );
        }
    }

}