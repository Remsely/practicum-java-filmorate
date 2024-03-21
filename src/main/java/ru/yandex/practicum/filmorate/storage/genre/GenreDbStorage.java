package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
        String sqlQuery = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) ->
                new Genre(id, rs.getString("name")), id);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("name"))
        );
    }

    @Override
    public List<Genre> getFilmGenres(long id) {
        String sqlQuery = "select * from films_genres where film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> this.get(rs.getLong("genre_id")), id);
    }

    @Override
    public List<Genre> addFilmGenres(long id, List<Genre> genres) {
        for (Genre genre : genres) {
            String sqlQuery = "insert into FILMS_GENRES (film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, id, genre.getId());
        }
        return genres;
    }

    @Override
    public boolean notContainGenre(long id) {
        String sqlQuery = "select count(*) from GENRES where GENRE_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }
}
