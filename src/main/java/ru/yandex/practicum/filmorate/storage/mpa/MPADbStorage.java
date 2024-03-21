package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA get(long id) {
        if (this.notContainMPA(id)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("MPA id", String.format("Не найден рейтиг с ID: %d.", id))
            );
        }
        String sqlQuery = "select * from mpa_ratings where rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> new MPA(id, rs.getString("name")), id);
    }

    @Override
    public List<MPA> getAll() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new MPA(rs.getLong("rating_id"), rs.getString("name"))
        );
    }

    @Override
    public boolean notContainMPA(long id) {
        String sqlQuery = "select count(*) from mpa_ratings where rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count == 0;
    }
}
