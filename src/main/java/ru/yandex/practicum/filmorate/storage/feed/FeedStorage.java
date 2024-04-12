package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;

public interface FeedStorage {
    void add(long userId, long entityId, FeedEventType eventType, FeedOperation operation);

    Long getOperationIndex(FeedOperation operation);

    Long getEventTypeIndex(FeedEventType eventType);
}
