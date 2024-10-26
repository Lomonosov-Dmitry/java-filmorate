package ru.yandex.practicum.filmorate.service;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToFriends(Integer userId, Integer friendId) {
        log.info("Добавляем {} в друзья к {}", userId, friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return user;
    }

    public User deleteFromFriends(Integer userId, Integer friendId) {
        log.info("Удаляем {} из друзей у {}", friendId, userId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        return user;
    }

    public Collection<User> getFriends(Integer userId) {
        log.info("Получаем всех друзей у {}", userId);
        User user = userStorage.getUserById(userId);
        Collection<User> friends = new ArrayList<>();
        for (int id : user.getFriends()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(Integer userId, Integer otherId) {
        log.info("Получаем общих друзей у {} и {}", userId, otherId);
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        List<Integer> mutualFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .toList();
        List<User> result = new ArrayList<>();
        if (!mutualFriends.isEmpty()) {
            for (Integer id : mutualFriends) {
                result.add(userStorage.getUserById(id));
            }
        }
        return result;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Integer delete(Integer userId) {
        return userStorage.delete(userId);
    }
}
