package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long reviewId;
    private String content;
    private Long userId;
    private Long filmId;

    @JsonProperty("isPositive")
    private Boolean isPositive;

    // Расчетное (!) значение полезности отзыва
    private Long useful;
}
