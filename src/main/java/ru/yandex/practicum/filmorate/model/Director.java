package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private final long id;

    @NotBlank(message = "Имя не может быть пустым.")
    private final String name;
}
