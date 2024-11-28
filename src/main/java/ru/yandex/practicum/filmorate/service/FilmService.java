package ru.yandex.practicum.filmorate.service;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.UserStorage;

import java.util.*;


@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = (Logger) LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(@Qualifier("SqlFilmStorage") FilmStorage filmStorage, @Qualifier("SqlUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmDto likeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        film.getLikes().add(userId);
        filmStorage.update(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto dislikeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikes().isEmpty()) {
            userStorage.getUserById(userId);
            film.getLikes().remove(userId);
            filmStorage.update(film);
        }
        return FilmMapper.mapToFilmDto(film);
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilmById(Integer filmId) {
        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(filmId));
    }

    public Collection<Film> getPopular(Integer count) {

        if (count <= 0) {
            throw new ValidationException("Передано неверное значение count", "Значение должно быть больше нуля");
        }

        List<Film> sorted = new ArrayList<>(filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .toList());
        Collections.reverse(sorted);
        List<Film> popular = new ArrayList<>();
        log.info("Возвращаем рейтинг популярности");
        if (sorted.size() < count)
            return sorted;
        else {
            for (int i = 0; i < count; i++) {
                popular.add(sorted.get(i));
            }
            return popular;
        }
    }

    public FilmDto create(NewFilmRequest request) {
        return FilmMapper.mapToFilmDto(filmStorage.create(FilmMapper.mapToFilm(request)));
    }

    public FilmDto update(UpdateFilmRequest request) {
        return FilmMapper.mapToFilmDto(filmStorage.update(FilmMapper.mapUpdateToFilm(request)));
    }

    public Integer delete(Integer count) {
        return filmStorage.delete(count);
    }
}
