package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@AllArgsConstructor
@Getter
public class EntityNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;
}