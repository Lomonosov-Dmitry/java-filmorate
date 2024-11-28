package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setId(resultSet.getInt("Id"));
        rating.setName(resultSet.getString("Name"));
        return rating;
    }
}
