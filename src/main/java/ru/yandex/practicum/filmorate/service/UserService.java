package ru.yandex.practicum.filmorate.service;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return user;
    }

    public User deleteFromFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        return user;
    }

    public Collection<User> getFriends(Integer userId) {
        User user = userStorage.getUserById(userId);
        Collection<User> friends = new ArrayList<>();
        for (int id : user.getFriends()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUserById(userId);
        List<Integer> mutualFriends = user.getFriends().stream()
                .filter(userStorage.getUserById(otherId).getFriends()::contains)
                .toList();
        List<User> result = new ArrayList<>();
        if (!mutualFriends.isEmpty()) {
            for (Integer id : mutualFriends) {
                result.add(userStorage.getUserById(id));
            }
        }
        return result;
    }
}
