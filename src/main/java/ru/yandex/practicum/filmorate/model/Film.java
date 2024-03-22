package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.annotations.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длинна описания фильма - 200 символов.")
    private String description;

    private List<Genre> genres;

    private MPA mpa;

    @AfterDate(lowerBound = "1895-12-28", message = "Дата релиза фильма не может быть раньше 28.12.1895.")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private int duration;

    private Set<Long> likes;
}