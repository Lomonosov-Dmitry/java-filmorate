package ru.yandex.practicum.filmorate.dal;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.time.LocalDate;
import java.util.*;

@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = (Logger) LoggerFactory.getLogger(InMemoryFilmStorage.class);

    @Override
    public Film getFilmById(Integer filmId) {
        log.info("Ищем фильм с id = {}", filmId);
        existenseValidation(filmId);
        return films.get(filmId);
    }

    @Override
    public Film create(Film film) {
        log.info("Добавляем новый фильм {}", film.getName());
        Validator.releaseValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновляем фильм {}", film.getName());
        Validator.releaseValidation(film);
        existenseValidation(film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Integer delete(Integer filmId) {
        log.info("Удаляем фильм {}", filmId);
        existenseValidation(filmId);
        films.remove(filmId);
        return filmId;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Возвращаем все фильмы");
        return films.values().stream().toList();
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }

    private void existenseValidation(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Ошибка поиска фильма", "Фильм с id = " + filmId + " не найден");
        }
    }
}
