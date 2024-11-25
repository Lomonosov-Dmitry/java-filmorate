package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("Id"));
        film.setName(resultSet.getString("Name"));
        film.setDescription(resultSet.getString("Description"));
        film.setReleaseDate(LocalDate.parse(resultSet.getString("ReleaseDate")));
        film.setDuration(resultSet.getInt("Duration"));
        return film;
    }
}
