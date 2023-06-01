package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(long filmId, long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Long> getLikesForFilm(long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }
}
