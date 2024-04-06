package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;

public interface FeedStorage {
    // Не знаю, что лучше. Заводить такие мапы или получать каждый раз индексы типов эвентов при помощи методов
    // getOperationIndex и getEventTypeIndex...
    /*
    Map<FeedOperation, Integer> FEED_OPERATION_DB_INDEXES = Map.of(
            FeedOperation.ADD, 1,
            FeedOperation.UPDATE, 2,
            FeedOperation.REMOVE, 3
    );

    Map<FeedEventType, Integer> FEED_EVENT_TYPE_DB_INDEXES = Map.of(
            FeedEventType.LIKE, 1,
            FeedEventType.REVIEW, 2,
            FeedEventType.FRIEND, 3
    );*/

    void add(long userId, long entityId, FeedEventType eventType, FeedOperation operation);

    Long getOperationIndex(FeedOperation operation);

    Long getEventTypeIndex(FeedEventType eventType);
}
