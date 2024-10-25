package ru.yandex.practicum.filmorate.storage;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
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
        releaseValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновляем фильм {}", film.getName());
        releaseValidation(film);
        existenseValidation(film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        log.info("Удаляем фильм {}", film.getName());
        existenseValidation(film.getId());
        films.remove(film.getId());
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Возвращаем все фильмы");
        return films.values().stream().toList();
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        if (count <= 0) {
            log.error("Передано неверное значение count - {}", count);
            throw new ValidationException("Передано неверное значение count", "Значение должно быть больше нуля");
        }
        List<Film> sorted = new ArrayList<>(films.values().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .toList());
        Collections.reverse(sorted);
        List<Film> popular = new ArrayList<>();
        if (sorted.size() < count)
            return sorted;
        else {
            for (int i = 0; i < count; i++) {
                popular.add(sorted.get(i));
            }
            return popular;
        }
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
            log.error("Дата релиза должна быть позже 28.12.1895, а передано {}", film.getReleaseDate());
            throw new ValidationException("Передана неверная дата релиза", "Дата релиза должна быть позже 28.12.1895");
        }
    }

    private void existenseValidation(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Ошибка поиска фильма", "Фильм с id = " + filmId + " не найден");
        }
    }
}
