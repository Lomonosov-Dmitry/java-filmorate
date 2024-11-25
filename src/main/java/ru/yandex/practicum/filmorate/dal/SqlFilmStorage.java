package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.rowmappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.dal.rowmappers.FilmLikesRowMapper;
import ru.yandex.practicum.filmorate.dal.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.rowmappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository("SqlFilmStorage")
public class SqlFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM Users WHERE Id = ?";
    private static final String GET_FILM_LIKES = "SELECT User_Id FROM Likes WHERE Film = ?";
    private static final String DELETE_FILM_LIKES = "DELETE FROM Likes WHERE Film = ?";
    private static final String INSERT_FILM_LIKE = "INSERT INTO LIKES(Film, User_Id) VALUES (?, ?)";
    private static final String INSERT_QUERY = "INSERT INTO Films(Name, Description, ReleaseDate, Duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE Films SET Name = ?, Description = ?, ReleaseDate = ?, Duration = ? WHERE id = ?";
    private static final String GET_FILM_GENRES = "SELECT Genre FROM Film_Genre WHERE Film = ?";
    private static final String DELETE_FILM_GENRES = "DELETE FROM Film_Genre WHERE Film = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO Film_Genre(Film, Genre) VALUES (?, ?)";
    private static final String CHECK_MPA = "SELECT * FROM Ratings WHERE Id = ?";
    private static final String CHECK_USER = "SELECT * FROM Users WHERE Id = ?";
    private static final String CHECK_GENRE = "SELECT * FROM Genres WHERE Id = ?";

    @Autowired
    public SqlFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate() != null)
            releaseValidation(film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (film.getMpa() != null) {
            if (!checkMpa(film.getMpa().getId()))
                throw new ValidationException("Не найдено", "Не найден рейтинг с ID = " + film.getMpa());
            else {
                jdbcTemplate.update("UPDATE Films SET Rating_Id = ? WHERE ID = ?", film.getMpa().getId(), film.getId());
                film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM Ratings WHERE Id = ?", new RatingRowMapper(), film.getMpa().getId()));
            }
        }
        if (!film.getGenres().isEmpty()) {
            insertGenres(film);
        }
        if (!film.getLikes().isEmpty()) {
            insertLikes(film);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        if (!checkFilm(filmId))
            throw new NotFoundException("Не найдено", "Не найден фильм с ID = " + filmId);
        jdbcTemplate.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                filmId);
        if (film.getMpa() != null) {
            if (!checkMpa(film.getMpa().getId()))
                throw new ValidationException("Не найдено", "Не найден рейтинг с ID = " + film.getMpa());
            else {
                jdbcTemplate.update("UPDATE Films SET Rating_Id = ? WHERE ID = ?", film.getMpa().getId(), film.getId());
                film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM Ratings WHERE Id = ?", new RatingRowMapper(), film.getMpa().getId()));
            }
        }
        List<Integer> oldLikes = getLikes(filmId);
        if (film.getLikes().isEmpty() && oldLikes != null)
            jdbcTemplate.update(DELETE_FILM_LIKES, filmId);
        if (!film.getLikes().equals(oldLikes)) {
            jdbcTemplate.update(DELETE_FILM_LIKES, filmId);
            insertLikes(film);
        }
        List<Integer> oldGenres = getGenres(filmId);
        if (film.getGenres().isEmpty() && oldGenres != null)
            jdbcTemplate.update(DELETE_FILM_GENRES, filmId);

        return film;
    }

    @Override
    public Integer delete(Integer filmId) {
        jdbcTemplate.update(DELETE_QUERY, filmId);
        return filmId;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films;
        try {
             films = jdbcTemplate.query(FIND_ALL_QUERY, new FilmRowMapper());
        } catch (EmptyResultDataAccessException ex) {
             films = null;
        }
        if (films != null) {
            for (Film film : films) {
                film.setLikes(getLikes(film.getId()));
                film.setGenres(getGenres(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            Film film = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new FilmRowMapper(), filmId);
            film.setLikes(getLikes(filmId));
            film.setGenres(getGenres(filmId));
            return film;
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException("Не найдено", "Не найден фильм с ID = " + filmId);
        }
    }

    private List<Integer> getLikes(Integer filmId) {
        try {
            return jdbcTemplate.query(GET_FILM_LIKES, new FilmLikesRowMapper(), filmId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void insertLikes(Film film) {
        for (Integer userId : film.getLikes()) {
            if (!checkUser(userId))
                throw new ValidationException("Не найдено", "Не найден пользователь с ID = " + userId);
            else
                jdbcTemplate.update(INSERT_FILM_LIKE, film.getId(), userId);
        }
    }

    private List<Integer> getGenres(Integer filmId) {
        try {
            return jdbcTemplate.query(GET_FILM_GENRES, new FilmGenreRowMapper(), filmId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void insertGenres(Film film) {
        for (Integer genre : film.getGenres()) {
            if (!checkGenre(genre))
                throw new ValidationException("Не найдено", "Не найден жанр с ID = " + genre);
            else
                jdbcTemplate.update(INSERT_FILM_GENRE, film.getId(), genre);
        }
    }

    private boolean checkFilm(Integer filmId) {
        try {
            jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new FilmRowMapper(), filmId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private boolean checkMpa(Integer mpaId) {
        try {
            jdbcTemplate.queryForObject(CHECK_MPA, new RatingRowMapper(), mpaId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private boolean checkUser(Integer userId) {
        try {
            jdbcTemplate.queryForObject(CHECK_USER, new RatingRowMapper(), userId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private boolean checkGenre(Integer genreId) {
        try {
            jdbcTemplate.queryForObject(CHECK_GENRE, new RatingRowMapper(), genreId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    private void releaseValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Передана неверная дата релиза", "Дата релиза должна быть позже 28.12.1895");
        }
    }
}
