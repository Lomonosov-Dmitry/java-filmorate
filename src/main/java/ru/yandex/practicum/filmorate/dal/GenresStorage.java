package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.rowmappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Repository
public class GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_QUERY = "INSERT INTO Genres(Name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE Genres SET Name = ? WHERE Id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM Genres";
    private static final String SELECT_ONE_QUERY = "SELECT * FROM Genres WHERE Id = ?";
    private static final String DELETE_QUERY = "DELETE FROM Genres WHERE Id = ?";

    @Autowired
    public GenresStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre create(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        genre.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return genre;
    }

    public Genre update(Genre genre) {
        jdbcTemplate.update(UPDATE_QUERY
                , genre.getName()
                , genre.getId());
        return genre;
    }

    public Collection<Genre> findAll() {
        return jdbcTemplate.query(SELECT_ALL_QUERY, new GenreRowMapper());
    }

    public Genre getMpaById(Integer genreId) {
        try {
            return jdbcTemplate.queryForObject(SELECT_ONE_QUERY, new GenreRowMapper(), genreId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Не найдено", "Не найден фильм с ID = " + genreId);
        }
    }

    public Integer delete(Integer genreId) {
        jdbcTemplate.update(DELETE_QUERY, genreId);
        return genreId;
    }
}
