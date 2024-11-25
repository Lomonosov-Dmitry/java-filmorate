package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateFilmRequest {
    private int id;
    @NotNull(message = "Название не должно быть null")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private ShortMpa mpa;
    private List<ShortGenre> genres = new ArrayList<>();
    private List<ShortLike> likes = new ArrayList<>();
}

