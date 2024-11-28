package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            Rating mpa = new Rating();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }
        if (!request.getGenres().isEmpty()) {
            film.setGenres(request.getGenres()
                    .stream()
                    .map(ShortGenre::getId)
                            .map(id -> {
                                Genre genre = new Genre();
                                genre.setId(id);
                                return genre;
                            })
                    .toList());
        }
        if (!request.getLikes().isEmpty()) {
            film.setLikes(request.getLikes()
                    .stream()
                    .map(ShortLike::getId)
                    .toList());
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        if (film.getMpa() != null) {
            dto.setMpa(film.getMpa());
        }
        if (!film.getGenres().isEmpty()) {
            dto.setGenres(film.getGenres());
        }
        if (!film.getLikes().isEmpty()) {
            List<ShortLike> likes = new ArrayList<>();
            for (Integer likeId : film.getLikes()) {
                ShortLike like = new ShortLike();
                like.setId(likeId);
                likes.add(like);
            }
            dto.setLikes(likes);
        }
        return dto;
    }

    public static Film mapUpdateToFilm(UpdateFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            Rating mpa = new Rating();
            mpa.setId(request.getMpa().getId());
            film.setMpa(mpa);
        }
        if (!request.getGenres().isEmpty()) {
            film.setGenres(request.getGenres()
                    .stream()
                    .map(ShortGenre::getId)
                    .map(id -> {
                        Genre genre = new Genre();
                        genre.setId(id);
                        return genre;
                    })
                    .toList());
        }
        if (!request.getLikes().isEmpty()) {
            film.setLikes(request.getLikes()
                    .stream()
                    .map(ShortLike::getId)
                    .toList());
        }
        return film;
    }
}
