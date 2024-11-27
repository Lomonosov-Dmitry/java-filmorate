package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

    @Autowired
    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAll() {
        return genresService.findAll();
    }

    @GetMapping("/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getMpaById(@PathVariable("genreId") Integer genreId) {
        return genresService.findOne(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre create(@Valid @RequestBody Genre genre) {
        return genresService.create(genre);
    }

    @PutMapping
    public Genre update(@Valid @RequestBody Genre genre) {
        return genresService.update(genre);
    }

    @DeleteMapping("/{genreId}")
    public Integer delete(@PathVariable("genreId") Integer mpaId) {
        return genresService.delete(mpaId);
    }
}
