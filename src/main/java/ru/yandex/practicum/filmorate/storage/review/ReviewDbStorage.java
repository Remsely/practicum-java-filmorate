package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Review> add(Review review) {
        String sqlQuery =
                "INSERT INTO review (content, user_id, film_id, is_positive) " +
                        "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setLong(2, review.getUserId());
            stmt.setLong(3, review.getFilmId());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return getReview(id);
    }

    public Optional<Review> update(Review review) {
        String sqlQuery =
                "UPDATE review SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        return getReview(review.getId());
    }

    public Review delete(long id) {
        Optional<Review> reviewOptional = getReview(id);

        String sqlQuery = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, id);

        return reviewOptional.orElse(null);
    }

    public Optional<Review> getReview(long id) {
        String sqlQuery = getBaseCommand() +
                "WHERE review_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Review> getAllReviews(int count) {
        String sqlQuery = getBaseCommand() +
                "ORDER BY useful DESC " +
                "LIMIT ? ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    public List<Review> getFilmReviews(Long filmId, int count) {
        String sqlQuery = getBaseCommand() +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ? ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    public void addLike(long id, long userId) {
        String sqlQuery =
                "MERGE INTO like_review (user_id, review_id, usefull) VALUES (?, ?, true)";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    public void addDislike(long id, long userId) {
        String sqlQuery =
                "MERGE INTO like_review (user_id, review_id, usefull) VALUES (?, ?, false)";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    public void removeLike(long id, long userId) {
        String sqlQuery =
                "DELETE FROM like_review WHERE user_id = ? AND review_id = ?";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    public void removeDislike(long id, long userId) {
        String sqlQuery =
                "DELETE FROM like_review WHERE user_id = ? AND review_id = ?";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    public boolean notContainReview(long reviewId) {
        String sqlQuery = "SELECT review_id FROM review WHERE review_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        return !rows.next();
    }

    private String getBaseCommand() {
        return
                "SELECT \n " +
                "  review_id, content, user_id, film_id, is_positive, \n " +
                "  IFNULL((SELECT sum(CASE usefull WHEN true THEN 1 ELSE -1 END) \n " +
                "          FROM like_review \n " +
                "          WHERE review_id = r.review_id), \n " +
                        " 0) AS useful \n " +
                "FROM review as r \n ";
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getLong("useful"))
                .build();
    }
}
