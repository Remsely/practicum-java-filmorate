package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MPAStorage mpaStorage;

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, rating_id, release, duration) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                    statement.setString(1, film.getName());
                    statement.setString(2, film.getDescription());

                    if (film.getMpa() == null) {
                        statement.setNull(3, Types.INTEGER);
                    } else {
                        statement.setLong(3, film.getMpa().getId());
                    }

                    statement.setDate(4, Date.valueOf(film.getReleaseDate()));
                    statement.setInt(5, film.getDuration());
                    return statement;
                },
                keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        genreStorage.addFilmGenres(id, film.getGenres());

        return this.get(id);
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery =
                "UPDATE films " +
                        "SET name = ?, " +
                        "    description = ?, " +
                        "    rating_id = ?, " +
                        "    release = ?, " +
                        "    duration = ? " +
                        "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                id);
        return this.get(film.getId());
    }

    @Override
    public Film get(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String filmSqlQuery = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(filmSqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addLike(long id, long userId) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return this.get(id);
    }

    @Override
    public Film removeLike(long id, long userId) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return this.get(id);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery =
                "SELECT f.* " +
                        "FROM films AS f " +
                        "LEFT JOIN ( " +
                        "    SELECT film_id, COUNT(*) AS like_count " +
                        "    FROM likes " +
                        "    GROUP BY film_id " +
                        ") l ON f.film_id = l.film_id " +
                        "ORDER BY l.like_count DESC " +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public Set<Long> getLikes(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return (new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), id)));
    }

    @Override
    public boolean notContainFilm(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release").toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(genreStorage.getFilmGenres(id))
                .mpa(mpaStorage.get(rs.getLong("rating_id")))
                .likes(this.getLikes(id))
                .build();
    }
}