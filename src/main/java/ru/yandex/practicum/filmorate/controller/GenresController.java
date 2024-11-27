package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenresController {
    private final GenresService genresService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(GenresController.class);

    @Autowired
    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAll() {
        log.info("Запрашиваем все жанры");
        return genresService.findAll();
    }

    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenreById(@PathVariable("genreId") Integer genreId) {
        log.info("Запрашиваем жанр с ID = {}", genreId);
        return genresService.findOne(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre create(@Valid @RequestBody Genre genre) {
        log.info("Создаем новый жанр с названием {}", genre.getName());
        return genresService.create(genre);
    }

    @PutMapping
    public Genre update(@Valid @RequestBody Genre genre) {
        log.info("Обновляем жанр с ID = {}", genre.getId());
        return genresService.update(genre);
    }

    @DeleteMapping("/{genreId}")
    public Integer delete(@PathVariable("genreId") Integer genreId) {
        log.info("Удаляем жанр с ID = {}", genreId);
        return genresService.delete(genreId);
    }
}
