package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавляем новый фильм {}", film.getName());
        releaseValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм {}", film.getName());
        releaseValidation(film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id = {} не найден", film.getId());
            throw new NotFoundException("Такой фильм не найден");
        }
        films.replace(film.getId(), film);
        return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }

    private void releaseValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза должна быть позже 28.12.1895");
            throw new ValidationException("Дата релиза должна быть позже 28.12.1895");
        }
    }
}
