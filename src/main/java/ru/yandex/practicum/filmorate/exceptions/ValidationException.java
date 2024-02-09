package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends IllegalArgumentException {
    public ValidationException(String s) {
        super(s);
    }
}