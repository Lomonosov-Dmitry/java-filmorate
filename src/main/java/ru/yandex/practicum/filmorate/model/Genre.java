package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Genre {
    private int id;
    @NotNull(message = "Название не должно быть null")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}
