package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA get(long id) {
        String sqlQuery = "select * from mpa_ratings where rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> new MPA(id, rs.getString("name")), id);
    }

    @Override
    public List<MPA> get() {
        String sqlQuery = "select * from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new MPA(rs.getLong("rating_id"), rs.getString("name"))
        );
    }
}
