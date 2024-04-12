package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.FeedEventType;
import ru.yandex.practicum.filmorate.model.feed.FeedOperation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(
            ReviewStorage reviewStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
    }

    public Review addReview(Review review) {
        validation(review);
        checkUserExists(review.getUserId());
        long userId = review.getUserId();
        long filmId = review.getFilmId();

        checkUserExists(userId);
        checkFilmExists(review.getFilmId());
        Optional<Review> savedReview = reviewStorage.add(review);
        if (savedReview.isEmpty()) {
            throwReviewNotExists(review.getId());
        }
        log.info("Отзыв добавлен: {}", savedReview);

        long reviewId = savedReview.get().getId();
        FeedEventType eventType = FeedEventType.REVIEW;
        FeedOperation operation = FeedOperation.ADD;

        feedStorage.add(userId, reviewId, eventType, operation);
        log.debug(
                "Добавление отзыва пользователя с id {} о фильме с id {} добавлен в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                userId, filmId, userId, reviewId, eventType, operation
        );
        return savedReview.get();
    }

    public Review updateReview(Review review) {
        long reviewId = review.getId();
        long filmId = review.getFilmId();

        validation(review);
        Optional<Review> savedReview = reviewStorage.update(review);
        if (savedReview.isEmpty()) {
            throwReviewNotExists(review.getId());
        }
        log.info("Отзыв обновлен: {}", savedReview);

        long userId = savedReview.get().getUserId();

        FeedEventType eventType = FeedEventType.REVIEW;
        FeedOperation operation = FeedOperation.UPDATE;

        feedStorage.add(userId, reviewId, eventType, operation);
        log.debug(
                "Обновление отзыва пользователя с id {} о фильме с id {} добавлен в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                userId, filmId, userId, reviewId, eventType, operation
        );
        return savedReview.get();
    }

    public void deleteReview(long id) {
        checkReviewExists(id);

        Review review = reviewStorage.delete(id);
        log.info("Отзыв с id {} удален", id);

        long userId = review.getUserId();
        long filmId = review.getFilmId();

        FeedEventType eventType = FeedEventType.REVIEW;
        FeedOperation operation = FeedOperation.REMOVE;

        feedStorage.add(userId, id, eventType, operation);
        log.debug(
                "Удаление отзыва пользователя с id {} о фильме с id {} добавлен в таблицу feed. " +
                        "userId = {}, entityId = {}, eventType = {}, operation = {}",
                userId, filmId, userId, id, eventType, operation
        );
    }

    public Review getReview(long id) {
        Optional<Review> review = reviewStorage.getReview(id);
        if (review.isEmpty()) {
            throwReviewNotExists(id);
        }
        log.info("Получен отзыв с id {}. Review: {}", id, review);
        return review.get();
    }

    public List<Review> getAllReviews(int count) {
        List<Review> reviews = reviewStorage.getAllReviews(count);
        log.info("Получен список отзывов на все фильмы (count {}). List : {}", count, reviews);
        return reviews;
    }

    public List<Review> getFilmReviews(long filmId, int count) {
        List<Review> reviews = reviewStorage.getFilmReviews(filmId, count);
        log.info("Получен список отзывов на фильм с id {} (count {})", filmId, count);
        return reviews;
    }

    public void addLike(long id, long userId) {
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.addLike(id, userId);
        log.info("Пользователь с userId {} поставил лайк фильму с filmId {}", userId, id);
    }

    public void addDislike(long id, long userId) {
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.addDislike(id, userId);
        log.info("Пользователь с userId {} поставил дизлайк фильму с filmId {}", userId, id);
    }

    public void removeLike(long id, long userId) {
        // Здесь эти проверки не особо то и нужны, но пусть будут
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.removeLike(id, userId);
        log.info("Пользователь с userId {} удалил лайк к фильму с filmId {}", userId, id);
    }

    public void removeDislike(long id, long userId) {
        // Здесь эти проверки не особо то и нужны, но пусть будут
        checkReviewExists(id);
        checkUserExists(userId);
        reviewStorage.removeDislike(id, userId);
        log.info("Пользователь с userId {} удалил дизлайк к фильму с filmId {}", userId, id);
    }

    private void checkUserExists(long userId) {
        if (userStorage.notContainUser(userId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("User id", String.format("Не найден пользователь с ID: %d.", userId))
            );
        }
    }

    private void checkFilmExists(long filmId) {
        if (filmStorage.notContainFilm(filmId)) {
            throw new EntityNotFoundException(
                    new ErrorResponse("Film id", String.format("Не найден фильм с ID: %d.", filmId))
            );
        }
    }

    private void checkReviewExists(long reviewId) {
        if (reviewStorage.notContainReview(reviewId)) {
            throwReviewNotExists(reviewId);
        }
    }

    private void throwReviewNotExists(long reviewId) {
        throw new EntityNotFoundException(
                new ErrorResponse("Review id", String.format("Не найден отзыв с ID: %d.", reviewId)));
    }

    private void validation(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Не заполнено содержание отзыва");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("Не указан код пользователя");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Не указан код фильма");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Не указан тип отзыва");
        }
    }

}
