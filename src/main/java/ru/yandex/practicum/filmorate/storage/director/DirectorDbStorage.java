package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmAttributeNotExistOnFilmCreationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director get(long id) {
        if (this.notContainDirector(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Director id", String.format("Не найден режиссер с ID: %d.", id))
            );
        }
        String sqlQuery = "SELECT * FROM director WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) ->
                Director.builder()
                        .id(rs.getLong("director_id"))
                        .name(rs.getString("name"))
                        .build(), id);
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "INSERT INTO director (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        long id = director.getId();
        if (this.notContainDirector(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Director id", String.format("Не найден режиссер с ID: %d.", id))
            );
        }
        String sqlQuery = "UPDATE director SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), id);
        return this.get(director.getId());
    }

    @Override
    public void delete(long id) {
        if (this.notContainDirector(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Director id", String.format("Не найден режиссер с ID: %d.", id))
            );
        }
        String sqlQuery = "DELETE FROM director WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM director";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build());
    }

    // Добавить режиссеров к фильму
    @Override
    public List<Director> addDirectors(long id, List<Director> directors) {
        List<Director> addedDirectors = new ArrayList<>();

        if (directors != null && !directors.isEmpty()) {
            for (Director director : directors) {
                long directorId = director.getId();

                if (notContainDirector(directorId)) {
                    throw new FilmAttributeNotExistOnFilmCreationException(
                            new ErrorResponse("Director id", String.format("Не найден режиссер с Id: %d.", id))
                    );
                }

                if (filmNotContainDirector(id, directorId)) {
                    String sqlQuery = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
                    jdbcTemplate.update(sqlQuery, id, directorId);
                    addedDirectors.add(director);
                }
            }
        }
        return addedDirectors;
    }

    // Получить режиссеров фильма
    @Override
    public List<Director> getFilmDirectors(long id) {
        String sqlQuery = "SELECT * FROM film_director WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> get(rs.getLong("director_id")), id);
    }

    // Удалить все связи режиссеров с фильмом
    @Override
    public void deleteFilmDirectors(long filmId) {
        String sqlQuery = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public boolean notContainDirector(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM director WHERE director_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    @Override
    public boolean filmNotContainDirector(long filmId, long directorId) {
        String sqlQuery = "SELECT COUNT(*) FROM film_director WHERE director_id = ? AND film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, directorId, filmId);
        return count != null && count == 0;
    }
}
