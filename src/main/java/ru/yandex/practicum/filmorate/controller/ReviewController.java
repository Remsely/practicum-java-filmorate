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

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        log.info("Получен POST-запрос: /reviews. Тело запроса: {}", review);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Получен PUT-запрос: /reviews. Тело запроса: {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Получен DELETE-запрос: /reviews/{}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable long id) {
        log.info("Получен GET-запрос: /reviews/{}", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviews(
                @RequestParam(required = false) Long filmId,
                @RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET-запрос: /reviews?filmId={}&count={}", filmId, count);

        if (filmId == null) {
            return reviewService.getAllReviews(count);
        } else {
            return reviewService.getFilmReviews(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен PUT-запрос: /reviews/{}/like/{}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен PUT-запрос: /reviews/{}/dislike/{}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен DELETE-запрос: /reviews/{}/like/{}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен DELETE-запрос: /reviews/{}/dislike/{}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
