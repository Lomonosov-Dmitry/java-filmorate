package ru.yandex.practicum.filmorate.validators;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public final class Validator {

    public static boolean checkOne(JdbcTemplate jdbc, String query, RowMapper mapper, int id) {
        try {
            jdbc.queryForObject(query, mapper, id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    public static void releaseValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Передана неверная дата релиза", "Дата релиза должна быть позже 28.12.1895");
        }
    }
}
