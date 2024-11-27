package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class LikesTable {
    private int film;
    private int userId;
}
