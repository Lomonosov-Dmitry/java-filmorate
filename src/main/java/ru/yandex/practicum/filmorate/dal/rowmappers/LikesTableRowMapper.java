package ru.yandex.practicum.filmorate.dal.rowmappers;

import ru.yandex.practicum.filmorate.dto.LikesTable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikesTableRowMapper implements RowMapper<LikesTable> {

    @Override
    public LikesTable mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LikesTable table = new LikesTable();
        table.setFilm(resultSet.getInt("Film"));
        table.setUserId(resultSet.getInt("User_Id"));
        return table;
    }
}
