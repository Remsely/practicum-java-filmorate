package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
    @JsonProperty("reviewId")
    private Long id;

    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    private String content;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;

    // Расчетное значение полезности отзыва
    private Long useful;
}
