package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenresStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
public class GenresService {
    private final GenresStorage genresStorage;

    @Autowired
    public GenresService(GenresStorage genresStorage) {
        this.genresStorage = genresStorage;
    }

    public Genre create(Genre genre) {
        return genresStorage.create(genre);
    }

    public Genre update(Genre genre) {
        return genresStorage.update(genre);
    }

    public Integer delete(Integer genreId) {
        return genresStorage.delete(genreId);
    }

    public Collection<Genre> findAll() {
        return genresStorage.findAll();
    }

    public Genre findOne(Integer genreId) {
        return genresStorage.getMpaById(genreId);
    }
}
