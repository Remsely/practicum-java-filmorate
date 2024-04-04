package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public ReviewService(
            ReviewDbStorage reviewStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {
        validation(review);
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        Review savedReview = reviewStorage.add(review);
        log.info("Отзыв добавлен: {}", savedReview);
        return savedReview;
    }

    // При обновлении отзыва проверка на валидность фильма и пользователя не производится,
    // т.к. пользователь может редактировать только свой отзыв на тот фильм, на который
    // был написан отзыв
    public Review updateReview(Review review) {
        validation(review);
        Review savedReview = reviewStorage.update(review);
        if (savedReview == null) {
            throwReviewNotExists(review.getReviewId());
        }
        log.info("Отзыв обновлен: {}", savedReview);
        return savedReview;
    }

    public void deleteReview(long id) {
        if (!reviewStorage.delete(id)) {
            throwReviewNotExists(id);
        }
        log.info("Отзыв с id {} удален", id);
    }

    public Review getReview(long id) {
        Review review = reviewStorage.getReview(id);
        if (review == null) {
            throwReviewNotExists(id);
        }
        log.info("Получен отзыв с id {}. Review: {}", id, review);
        return review;
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
