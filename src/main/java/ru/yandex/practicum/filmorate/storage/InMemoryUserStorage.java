package ru.yandex.practicum.filmorate.storage;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = (Logger) LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public User getUserById(Integer id) {
        log.info("Ищем пользователя с id = {}", id);
        existanceValidation(id);
        return users.get(id);
    }


    @Override
    public User create(User user) {
        log.info("Создаем нового пользователя {}", user.getLogin());
        loginValidation(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty())
            user.setName(user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        existanceValidation(user.getId());
        loginValidation(user);
        log.info("Обновляем пользователя {}", user.getLogin());
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        log.info("Удаляем пользователя {}", user.getLogin());
        existanceValidation(user.getId());
        users.remove(user.getId());
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values().stream().toList();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void loginValidation(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин не должен содержать пробелы");
            throw new ValidationException("Неверный логин", "Логин не должен содержать пробелы");
        }
    }

    private void existanceValidation(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с ID = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден", "Пользователь с id = " + userId + " не найден");
        }
    }
}
