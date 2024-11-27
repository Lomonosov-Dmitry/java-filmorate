package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
public class UserController {

    private final UserService userService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("Запрашиваем всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("Запрашиваем пользователя с ID = {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable Integer id) {
        log.info("Запрашиваем список друзей пользователя с ID = {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя с ID = {}", user.getId());
        return userService.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Добавляем пользователя {} в друзья к пользователю {}", friendId, id);
        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/users")
    public Integer delete(@RequestBody User user) {
        log.info("Удаляем пользователя с ID = {}", user.getId());
        return userService.delete(user.getId());
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Удаляем пользователя {} из друзей у пользователя {}", friendId, id);
        return userService.deleteFromFriends(id, friendId);
    }

}
