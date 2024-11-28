package ru.yandex.practicum.filmorate.dal;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.rowmappers.*;
import ru.yandex.practicum.filmorate.dto.FilmGenre;
import ru.yandex.practicum.filmorate.dto.LikesTable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository("SqlFilmStorage")
public class SqlFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = (Logger) LoggerFactory.getLogger(SqlFilmStorage.class);

    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM Users WHERE Id = ?";
    private static final String GET_FILM_LIKES = "SELECT User_Id FROM Likes WHERE Film = ?";
    private static final String DELETE_FILM_LIKES = "DELETE FROM Likes WHERE Film = ?";
    private static final String INSERT_FILM_LIKE = "INSERT INTO LIKES(Film, User_Id) VALUES (?, ?)";
    private static final String INSERT_QUERY = "INSERT INTO Films(Name, Description, ReleaseDate, Duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE Films SET Name = ?, Description = ?, ReleaseDate = ?, Duration = ? WHERE id = ?";
    private static final String GET_FILM_GENRES = "SELECT g.id, g.name " +
            "FROM GENRES AS g " +
            "JOIN film_genre AS fg ON g.id = fg.genre " +
            "WHERE fg.film=? GROUP BY g.ID";
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
            Validator.releaseValidation(film);
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
        log.info("Создали новый фильм с ID = {}", film.getId());
        if (film.getMpa() != null) {
            if (!Validator.checkOne(jdbcTemplate, CHECK_MPA, new RatingRowMapper(), film.getMpa().getId()))
                throw new ValidationException("Не найдено", "Не найден рейтинг с ID = " + film.getMpa());
            else {
                jdbcTemplate.update("UPDATE Films SET Rating_Id = ? WHERE ID = ?", film.getMpa().getId(), film.getId());
                film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM Ratings WHERE Id = ?", new RatingRowMapper(), film.getMpa().getId()));
                log.info("Добавили рейтинги фильму с ID = {}", film.getId());
            }
        }
        if (!film.getGenres().isEmpty()) {
            insertGenres(film);
            log.info("Добавили жанры фильму с ID = {}", film.getId());
        }
        if (!film.getLikes().isEmpty()) {
            insertLikes(film);
            log.info("Добавили лайки фильму с ID = {}", film.getId());
        }
        log.info("Вернули фильм с ID = {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        if (!Validator.checkOne(jdbcTemplate, FIND_BY_ID_QUERY, new FilmRowMapper(), filmId))
            throw new NotFoundException("Не найдено", "Не найден фильм с ID = " + filmId);
        jdbcTemplate.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                filmId);
        log.info("Обновили фильм с ID = {}", film.getId());
        if (film.getMpa() != null) {
            if (!Validator.checkOne(jdbcTemplate, CHECK_MPA, new RatingRowMapper(), film.getMpa().getId()))
                throw new ValidationException("Не найдено", "Не найден рейтинг с ID = " + film.getMpa());
            else {
                jdbcTemplate.update("UPDATE Films SET Rating_Id = ? WHERE ID = ?", film.getMpa().getId(), film.getId());
                film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM Ratings WHERE Id = ?", new RatingRowMapper(), film.getMpa().getId()));
                log.info("Обновили рейтинги фильму с ID = {}", film.getId());
            }
        }
        List<Integer> oldLikes = getLikes(filmId);
        if (film.getLikes().isEmpty() && oldLikes != null)
            jdbcTemplate.update(DELETE_FILM_LIKES, filmId);
        if (!film.getLikes().equals(oldLikes)) {
            jdbcTemplate.update(DELETE_FILM_LIKES, filmId);
            insertLikes(film);
            log.info("Обновили лайки фильму с ID = {}", film.getId());
        }
        List<Genre> oldGenres = getGenres(filmId);
        if (film.getGenres().isEmpty() && oldGenres != null)
            jdbcTemplate.update(DELETE_FILM_GENRES, filmId);
        log.info("Обновили жанры фильму с ID = {}", film.getId());
        log.info("Возвращаем фильм с ID = {}", film.getId());
        return film;
    }

    @Override
    public Integer delete(Integer filmId) {
        jdbcTemplate.update(DELETE_QUERY, filmId);
        log.info("Удалили фильм с ID = {}", filmId);
        return filmId;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Пришел запрос на все фильмы");
        List<Film> films;
        try {
            films = jdbcTemplate.query(FIND_ALL_QUERY, new FilmRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            films = null;
        }
        if (films != null) {
            log.info("Фильмы есть, заполняем список");
            List<LikesTable> likesTable;
            List<FilmGenre> allFilmsGenres;
            try {
                likesTable = jdbcTemplate.query("SELECT * FROM LIKES", new LikesTableRowMapper());
            } catch (EmptyResultDataAccessException ex) {
                likesTable = null;
            }
            try {
                allFilmsGenres = jdbcTemplate.query("SELECT * FROM FILM_GENRE", new FilmGenreRowMapper());
            } catch (EmptyResultDataAccessException ex) {
                allFilmsGenres = null;
            }
            for (Film film : films) {
                if (likesTable != null) {
                    log.info("Лайки к фильму с ID = {} есть, заполняем", film.getId());
                    film.setLikes(likesTable.stream()
                            .filter(like -> like.getFilm() == film.getId())
                            .map(LikesTable::getUserId)
                            .toList());
                    log.info("Лайки заполнили");
                }
                if (allFilmsGenres != null) {
                    log.info("Жанры к фильму с ID = {} есть, заполняем", film.getId());
                    film.setGenres(allFilmsGenres.stream()
                            .filter(filmGenre -> filmGenre.getFilmId() == film.getId())
                            .map((filmGenre) -> {
                                Genre genre = new Genre();
                                genre.setId(filmGenre.getGenreId());
                                return genre;
                            })
                            .toList());
                    log.info("Жанры заполнили");
                }
            }
        }
        log.info("Возвращаем фильмы");
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            Film film = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new FilmRowMapper(), filmId);
            film.setLikes(getLikes(filmId));
            film.setGenres(getGenres(filmId));
            if (film.getMpa() != null) {
                film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM Ratings WHERE Id = ?", new RatingRowMapper(), film.getMpa().getId()));
            }
            log.info("Возвращаем фильм c ID = {}", filmId);
            return film;
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationException("Не найдено", "Не найден фильм с ID = " + filmId);
        }
    }

    private List<Integer> getLikes(Integer filmId) {
        try {
            log.info("Получаем лайки для фильма с ID = {}", filmId);
            return jdbcTemplate.query(GET_FILM_LIKES, new FilmLikesRowMapper(), filmId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void insertLikes(Film film) {
        for (Integer userId : film.getLikes()) {
            if (!Validator.checkOne(jdbcTemplate, CHECK_USER, new UserRowMapper(), userId))
                throw new ValidationException("Не найдено", "Не найден пользователь с ID = " + userId);
            else
                jdbcTemplate.update(INSERT_FILM_LIKE, film.getId(), userId);
        }
    }

    private List<Genre> getGenres(Integer filmId) {
        try {
            return jdbcTemplate.query(GET_FILM_GENRES, new GenreRowMapper(), filmId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void insertGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            if (!Validator.checkOne(jdbcTemplate, CHECK_GENRE, new GenreRowMapper(), genre.getId()))
                throw new ValidationException("Не найдено", "Не найден жанр с ID = " + genre);
            else
                jdbcTemplate.update(INSERT_FILM_GENRE, film.getId(), genre.getId());
        }
    }

}
