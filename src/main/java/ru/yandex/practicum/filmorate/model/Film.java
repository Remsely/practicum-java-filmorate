package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длинна описания фильма - 200 символов.")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private int duration;

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 11, 28)))
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        this.releaseDate = releaseDate;
    }
}