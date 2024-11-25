package ru.yandex.practicum.filmorate.dal.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsRowMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(ResultSet result, int rowNum) throws SQLException {
        return result.getInt("Friend_Id");
    }
}
