package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmAttributeNotExistOnFilmCreationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre get(long id) {
        if (this.notContainGenre(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Genre id", String.format("Не найден жанр с ID: %d.", id))
            );
        }
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) ->
                new Genre(id, rs.getString("name")), id);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("name"))
        );
    }

    @Override
    public List<Genre> getFilmGenres(long id) {
        String sqlQuery = "SELECT * FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> this.get(rs.getLong("genre_id")), id);
    }

    @Override
    public List<Genre> addFilmGenres(long id, List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                long genreId = genre.getId();

                if (notContainGenre(genreId)) {
                    throw new FilmAttributeNotExistOnFilmCreationException(
                            new ErrorResponse("Genre id", String.format("Не найден жанр с ID: %d.", id))
                    );
                }

                if (filmNotContainGenre(id, genreId)) {
                    String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                    jdbcTemplate.update(sqlQuery, id, genre.getId());
                }
            }
        }
        return genres;
    }

    @Override
    public boolean notContainGenre(long id) {
        String sqlQuery = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }

    private boolean filmNotContainGenre(long filmId, long genreId) {
        String sqlQuery = "SELECT COUNT(*) FROM film_genre WHERE genre_id = ? AND film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, genreId, filmId);
        return count != null && count == 0;
    }
}