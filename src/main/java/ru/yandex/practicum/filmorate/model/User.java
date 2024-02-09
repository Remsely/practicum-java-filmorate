package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email(message = "Некорректный Email.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы.")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    public void setName(String name) {
        this.name = name.isBlank() ? login : name;
    }

    public void setLogin(String login) {
        this.login = login;
        if (name == null || name.isBlank())
            name = login;
    }
}