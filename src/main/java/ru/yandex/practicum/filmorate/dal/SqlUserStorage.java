package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.rowmappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.dal.rowmappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository
@Qualifier("SqlUserStorage")
public class SqlUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_QUERY = "SELECT * FROM Users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO Users(Email, Login, Name, Birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE Users SET Email = ?, Login = ?, Name = ?, Birthday = ? WHERE id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT Friend_Id FROM Friendship WHERE User_Id = ?";
    private static final String DELETE_QUERY = "DELETE FROM Users WHERE Id = ?";
    private static final String DELETE_USER_FRIENDS = "DELETE FROM Friendship WHERE User_Id = ?";
    private static final String ADD_FRIEND = "INSERT INTO Friendship(User_Id, Friend_Id) VALUES (?, ?)";

    public SqlUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (!checkUser(userId))
            throw new NotFoundException("Не найдено", "Не найден пользователь с ID = " + userId);
        jdbcTemplate.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                userId);
        jdbcTemplate.update(DELETE_USER_FRIENDS, userId);
        if (!user.getFriends().isEmpty()) {
            for (Integer friend : user.getFriends()) {
                jdbcTemplate.update(ADD_FRIEND, userId, friend);
            }
        }
        return user;
    }

    @Override
    public Integer delete(Integer userId) {
        jdbcTemplate.update(DELETE_QUERY, userId);
        return userId;
    }

    @Override
    public Collection<User> findAll() {
        List<User> users;
        try {
            users = jdbcTemplate.query(FIND_ALL_QUERY, new UserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            users = null;
        }
        if (users != null) {
            for (User user : users) {
                user.setFriends(getFriends(user.getId()));
            }
        }
        return users;
    }

    @Override
    public User getUserById(Integer userId) {
        try {
            User user = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new UserRowMapper(), userId);
            if (user != null)
                user.setFriends(getFriends(userId));
            return user;
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Не найдено", "Не найден пользователь с ID = " + userId);
        }
    }

    private List<Integer> getFriends(int userId) {
        return jdbcTemplate.query(GET_FRIENDS_QUERY, new FriendsRowMapper(), userId);
    }

    private boolean checkUser(Integer userId) {
        try {
            jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new UserRowMapper(), userId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }
}

