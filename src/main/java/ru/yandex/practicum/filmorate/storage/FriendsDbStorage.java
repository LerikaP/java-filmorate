package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }


    @Override
    public void addFriend(long userId, long friendId) {
        if (userDbStorage.checkIfUserIsInDb(userId) && userDbStorage.checkIfUserIsInDb(friendId)) {
            String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        } else {
            throw new NotFoundException("Пользователь " + userId + " и/или пользователь " + friendId +
                    " не найден.");
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        if (userDbStorage.checkIfUserIsInDb(userId) && userDbStorage.checkIfUserIsInDb(friendId)) {
            String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
        } else {
            throw new NotFoundException("Пользователь " + userId + " и/или пользователь " + friendId +
                    " не найден.");
        }
    }

    @Override
    public List<User> getFriends(long userId) {
            String sql = "SELECT * FROM user_friends WHERE user_id = ?";
            return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return userDbStorage.getUserById(rs.getLong("friend_id"));
    }
}
