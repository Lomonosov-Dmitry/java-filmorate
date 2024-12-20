package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    public Film create(Film film);

    public Film update(Film film);

    public Integer delete(Integer filmId);

    public Collection<Film> findAll();

    public Film getFilmById(Integer id);

}
