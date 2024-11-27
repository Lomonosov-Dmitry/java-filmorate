package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
public class FilmController {

    private final FilmService filmService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<FilmDto> findAll() {
        log.info("Запрашиваем все фильмы");
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        log.info("Запрашиваем фильм с ID = {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрашиваем фильмы по популярности");
        return filmService.getPopular(count);
    }

    @PostMapping("/films")
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        log.info("Создаем новый фильм");
        return filmService.create(request);
    }

    @PutMapping("/films")
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Обновляем фильм с ID = {}", request.getId());
        return filmService.update(request);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Фильм с ID = {} лайкает пользователь с ID = {}", id, userId);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films")
    public Integer delete(@RequestBody Film film) {
        log.info("Удаляем фильм с ID = {}", film.getId());
        return filmService.delete(film.getId());
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public FilmDto dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Фильм с ID = {} дизлайкает пользователь с ID = {}", id, userId);
        return filmService.dislikeFilm(id, userId);
    }
}
