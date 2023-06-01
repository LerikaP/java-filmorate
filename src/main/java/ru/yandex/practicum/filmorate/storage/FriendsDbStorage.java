package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

    }

    @Override
    public List<Long> getFriendsIds(long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
}
