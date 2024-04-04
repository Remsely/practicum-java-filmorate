package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Добавление нового отзыва
    // POST /reviews
    @PostMapping
    public Review addReview(@RequestBody Review review) {
        log.info("Получен POST-запрос: /reviews. Тело запроса: {}", review);
        return reviewService.addReview(review);
    }

    // Редактирование уже имеющегося отзыва
    // PUT /reviews
    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Получен PUT-запрос: /reviews. Тело запроса: {}", review);
        return reviewService.updateReview(review);
    }

    // Удаление уже имеющегося отзыва
    // DELETE /reviews/{id}
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Получен DELETE-запрос: /reviews/{}", id);
        reviewService.deleteReview(id);
    }

    // Получение отзыва по идентификатору
    // GET /reviews/{id}
    @GetMapping("/{id}")
    public Review getReview(@PathVariable long id) {
        log.info("Получен GET-запрос: /reviews/{}", id);
        return reviewService.getReview(id);
    }

    // Получение всех отзывов по идентификатору фильма, если фильм не указан, то все. Если кол-во не указано, то 10
    // GET /reviews?filmId={filmId}&count={count}
    @GetMapping
    public List<Review> getReviews(
                @RequestParam(defaultValue = "") String filmId,
                @RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET-запрос: /reviews?filmId={}&count={}", filmId, count);

        // Для получения списка отзывов здесь используется два метода. Можно было бы использовать один, и передавать
        // Optional-параметр для filmId, однако Idea на такие финты выдает предупреждение.
        // https://www.baeldung.com/java-optional#misuages
        if (filmId.isBlank()) {
            return reviewService.getAllReviews(count);
        } else {
            return reviewService.getFilmReviews(Long.parseLong(filmId), count);
        }
    }

    // Пользователь ставит лайк отзыву
    // PUT /reviews/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен PUT-запрос: /reviews/{}/like/{}", id, userId);
        reviewService.addLike(id, userId);
    }

    // Пользователь ставит дизлайк отзыву
    // PUT /reviews/{id}/dislike/{userId}
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен PUT-запрос: /reviews/{}/dislike/{}", id, userId);
        reviewService.addDislike(id, userId);
    }

    // Пользователь удаляет лайк отзыву
    // DELETE /reviews/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен DELETE-запрос: /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    // Пользователь удаляет дизлайк отзыву
    //DELETE /reviews/{id}/dislike/{userId}
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен DELETE-запрос: /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }

}
