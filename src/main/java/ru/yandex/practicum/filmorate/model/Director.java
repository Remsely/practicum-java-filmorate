package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {
    private long id;

    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
}
