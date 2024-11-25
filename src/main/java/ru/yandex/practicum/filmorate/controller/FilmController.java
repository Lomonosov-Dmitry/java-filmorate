package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopular(count);
    }

    @PostMapping("/films")
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        return filmService.create(request);
    }

    @PutMapping("/films")
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        return filmService.update(request);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films")
    public Integer delete(@RequestBody Film film) {
        return filmService.delete(film.getId());
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public FilmDto dislikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.dislikeFilm(id, userId);
    }
}
