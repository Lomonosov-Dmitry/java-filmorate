package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Film create(Film film);

    public Film update(Film film);

    public Film delete(Film film);

    public Collection<Film> findAll();

    public Film getFilmById(Integer id);

    public Collection<Film> getPopular(Integer count);
}
