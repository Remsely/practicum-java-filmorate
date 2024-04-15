package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Map<FeedOperation, Long> feedOperationIndexes = new HashMap<>();
    private final Map<FeedEventType, Long> feedEventTypeIndexes = new HashMap<>();

    @Override
    public void add(long userId, long entityId, FeedEventType eventType, FeedOperation operation) {
        final String sqlQuery = "INSERT INTO feed (user_id, entity_id, type_id, operation_id, time) " +
                "VALUES (?, ?, ?, ?, ?)";

        Long eventTypeIndex = feedEventTypeIndexes.computeIfAbsent(eventType, this::getEventTypeIndex);
        Long eventOperation = feedOperationIndexes.computeIfAbsent(operation, this::getOperationIndex);

        jdbcTemplate.update(sqlQuery,
                userId,
                entityId,
                eventTypeIndex,
                eventOperation,
                LocalDateTime.now()
        );
    }

    @Override
    public Long getOperationIndex(FeedOperation operation) {
        final String sqlQuery = "SELECT DISTINCT operation_id FROM event_operation WHERE name = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class, operation.toString());
    }

    @Override
    public Long getEventTypeIndex(FeedEventType eventType) {
        final String sqlQuery = "SELECT DISTINCT type_id FROM event_type WHERE name = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Long.class, eventType.toString());
    }
}