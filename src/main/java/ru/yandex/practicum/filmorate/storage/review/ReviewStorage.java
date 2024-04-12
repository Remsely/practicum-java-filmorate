package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> add(Review review);

    Optional<Review> update(Review review);

    Optional<Review> getReview(long id);

    List<Review> getAllReviews(int count);

    List<Review> getFilmReviews(long filmId, int count);

    Review delete(long id);

    void addLike(long id, long userId);

    void addDislike(long id, long userId);

    void removeLike(long id, long userId);

    void removeDislike(long id, long userId);

    boolean notContainReview(long reviewId);
}
