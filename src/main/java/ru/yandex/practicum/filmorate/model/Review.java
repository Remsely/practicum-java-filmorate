package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    private long reviewId;

    @NotBlank(message = "Описание не может быть пустым")
    private String content;

    @NotNull
    Long userId;

    @NotNull
    Long filmId;

    @NotNull
    @JsonProperty("isPositive")
    Boolean isPositive;

    // Расчетное значение полезности отзыва
    long useful;
}
