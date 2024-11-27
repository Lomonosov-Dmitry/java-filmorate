package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(MpaController.class);

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Rating> findAll() {
        log.info("Запрашиваем все рейтинги");
        return mpaService.fingAll();
    }

    @GetMapping("/{mpaId}")
    @ResponseStatus(HttpStatus.OK)
    public Rating getMpaById(@PathVariable("mpaId") Integer mpaId) {
        log.info("Запрашиваем рейтинг с ID = {}", mpaId);
        return mpaService.findOne(mpaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rating create(@Valid @RequestBody Rating rating) {
        log.info("Создаем новый рейтинг с именем {}", rating.getName());
        return mpaService.create(rating);
    }

    @PutMapping
    public Rating update(@Valid @RequestBody Rating rating) {
        log.info("Обновляем рейтинг с ID = {}", rating.getId());
        return mpaService.update(rating);
    }

    @DeleteMapping("/{mpaId}")
    public Integer delete(@PathVariable("mpaId") Integer mpaId) {
        log.info("Удаляем рейтинг с ID = {}", mpaId);
        return mpaService.delete(mpaId);
    }
}
