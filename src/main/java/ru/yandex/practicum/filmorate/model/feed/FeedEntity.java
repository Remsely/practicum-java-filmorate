package ru.yandex.practicum.filmorate.model.feed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedEntity {
    private long eventId;
    private long userId;
    private long entityId;
    private FeedEventType eventType;
    private FeedOperation operation;
    private long timestamp;
}
