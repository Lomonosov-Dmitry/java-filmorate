package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dto.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(resultSet.getInt("Film"));
        filmGenre.setGenreId(resultSet.getInt("Genre"));
        return filmGenre;
    }
}
