package ru.yandex.practicum.filmorate.dal;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.rowmappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.rowmappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Repository
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = (Logger) LoggerFactory.getLogger(MpaStorage.class);

    private static final String INSERT_QUERY = "INSERT INTO Ratings(Name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE Ratings SET Name = ? WHERE Id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM Ratings";
    private static final String SELECT_ONE_QUERY = "SELECT * FROM Ratings WHERE Id = ?";
    private static final String DELETE_QUERY = "DELETE FROM Ratings WHERE Id = ?";

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Rating create(Rating rating) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            stmt.setString(1, rating.getName());
            return stmt;
        }, keyHolder);
        rating.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Создали новый рейтинг с ID = {}", rating.getId());
        return rating;
    }

    public Rating update(Rating rating) {
        if (!Validator.checkOne(jdbcTemplate, SELECT_ONE_QUERY, new GenreRowMapper(), rating.getId()))
            throw new NotFoundException("Не найдено", "Не найден рейтинг с ID = " + rating.getId());
        else {
            jdbcTemplate.update(UPDATE_QUERY,
                    rating.getName(),
                    rating.getId());
            log.info("Обновили рейтинг с ID = {}", rating.getId());
            return rating;
        }
    }

    public Collection<Rating> findAll() {
        log.info("Возвращаем все рейтинги");
        return jdbcTemplate.query(SELECT_ALL_QUERY, new RatingRowMapper());
    }

    public Rating getMpaById(Integer mpaId) {
        try {
            log.info("Возвращаем рейтинг с ID = {}", mpaId);
            return jdbcTemplate.queryForObject(SELECT_ONE_QUERY, new RatingRowMapper(), mpaId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Не найдено", "Не найден рейтинг с ID = " + mpaId);
        }
    }

    public Integer delete(Integer mpaId) {
        jdbcTemplate.update(DELETE_QUERY, mpaId);
        log.info("Удалили рейтинг с ID = {}", mpaId);
        return mpaId;
    }
}
