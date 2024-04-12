package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmAttributeNotExistOnFilmCreationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
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
    private final DirectorStorage directorStorage;

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO film (name, description, rating_id, release, duration) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        long mpaId = film.getMpa().getId();
        if (mpaStorage.notContainMPA(mpaId)) {
            throw new FilmAttributeNotExistOnFilmCreationException(
                    new ErrorResponse("MPA id", String.format("Не найден MPA с ID: %d.", mpaId))
            );
        }

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
        directorStorage.addDirectors(id, film.getDirectors());
        return this.get(id);
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        checkFilmExist(id);

        String sqlQuery =
                "UPDATE film " +
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
        genreStorage.updateFilmGenres(id, film.getGenres());
        directorStorage.updateFilmDirectors(id, film.getDirectors());
        return this.get(film.getId());
    }

    @Override
    public Film get(long id) {
        checkFilmExist(id);
        String filmSqlQuery = "SELECT * FROM film WHERE film_id = ?";
        return jdbcTemplate.queryForObject(filmSqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void delete(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
        String sqlQuery = "DELETE FROM film WHERE film_id = " + id;
        jdbcTemplate.execute(sqlQuery);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM film";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> getFilmWithDirectorName(String name) {
        String nameStr = "%" + name.toLowerCase() + "%";
        String sqlQuery = "SELECT f.*\n" +
                "FROM FILM f  \n" +
                "JOIN FILM_DIRECTOR fd ON f.FILM_ID  = fd.FILM_ID \n" +
                "JOIN DIRECTOR d ON d.DIRECTOR_ID  = fd.DIRECTOR_ID \n" +
                "WHERE LOWER(d.NAME) LIKE ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, nameStr);
    }

    @Override
    public Film addLike(long id, long userId) {
        checkFilmExist(id);
        String sqlQuery = "MERGE INTO like_film (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return this.get(id);
    }

    @Override
    public Film removeLike(long id, long userId) {
        checkFilmExist(id);
        String sqlQuery = "DELETE FROM like_film WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return this.get(id);
    }

    @Override
    public Set<Long> getLikes(long id) {
        checkFilmExist(id);
        String sqlQuery = "SELECT user_id FROM like_film WHERE film_id = ?";
        return (new HashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), id)));
    }

    @Override
    public List<Film> getFilmWithName(String name) {
        String nameStr = "%" + name.toLowerCase() + "%";
        String sqlQuery = "SELECT * FROM film WHERE LOWER(name) LIKE ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, nameStr);
    }

    @Override
    public List<Film> getPopularFilm(int count, Long genreId, Integer year) {
        String sqlQuery = "SELECT fl.* " +
                "FROM film fl " +
                "LEFT JOIN like_film li ON li.film_id = fl.film_id " +
                (genreId != null ? "JOIN film_genre fg ON fl.film_id = fg.film_id" +
                        " WHERE fg.genre_id = " + genreId + " " : "") +
                (year != null ? (genreId != null ? "AND" : "WHERE") + " EXTRACT(YEAR FROM CAST(fl.release AS" +
                        " TIMESTAMP)) = " + year + " " : "") +
                "GROUP BY fl.film_id " +
                "ORDER BY COUNT(li.film_id) DESC " +
                "LIMIT " + count;
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> getDirectorSortedFilms(Long directorId, String sortBy) {
        if (directorId != null && directorStorage.notContainDirector(directorId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Director id", String.format("Не найден режиссер с ID: %d.", directorId))
            );
        }

        String sqlQuery = "SELECT f.film_id " +
                "FROM film f " +
                "JOIN film_director fd ON f.film_id = fd.film_id " +
                ("year".equals(sortBy) ? "WHERE fd.director_id = ? " +
                        "ORDER BY f.release" :
                        "LEFT JOIN like_film lf ON f.film_id = lf.film_id " +
                                "WHERE fd.director_id = ? " +
                                "GROUP BY f.film_id " +
                                "ORDER BY COUNT(lf.film_id) DESC");

        return jdbcTemplate.query(sqlQuery, new Object[]{directorId}, this::mapRowToSortedFilms);
    }

    @Override
    public boolean notContainFilm(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM film WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    private Film mapRowToSortedFilms(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        return get(id);
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
                .directors(directorStorage.getFilmDirectors(id))
                .mpa(mpaStorage.get(rs.getLong("rating_id")))
                .likes(this.getLikes(id))
                .build();
    }

    private void checkFilmExist(long id) {
        if (this.notContainFilm(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", id))
            );
        }
    }
}
