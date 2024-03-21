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
        String sqlQuery = "insert into films (name, description, rating_id, release, duration) values (?, ?, ?, ?, ?)";

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
        String sqlQuery = "update films set name = ?, description = ?, rating_id = ?, release = ?, duration = ? " +
                "where film_id = ?";
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
        String filmSqlQuery = "select * from films where film_id = ?";
        return jdbcTemplate.queryForObject(filmSqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addLike(long id, long userId) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "insert into LIKES (film_id, user_id) values (?, ?)";
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
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return this.get(id);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery = "select f.* " +
                "from FILMS as f " +
                "join (select FILM_ID, count(*) as like_count " +
                "from LIKES " +
                "group by FILM_ID " +
                "order by like_count desc " +
                "limit ?) l on f.FILM_ID = l.FILM_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public Set<Long> getLikes(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "select user_id from likes where film_id = ?";
        return (new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), id)));
    }

    @Override
    public boolean notContainFilm(long id) {
        String sqlQuery = "select count(*) from films where film_id = ?";
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
