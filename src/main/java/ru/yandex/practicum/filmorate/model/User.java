package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class User {
    private int id;
    @Email(message = "Некорректно указан email")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
}
