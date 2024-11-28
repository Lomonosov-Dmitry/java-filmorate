package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    private List<Integer> friends = new ArrayList<>();
    //private List<Integer> possibleFriends = new ArrayList<>();

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        if (friends.contains(friendId))
            friends.remove((Object) friendId);
    }
}
