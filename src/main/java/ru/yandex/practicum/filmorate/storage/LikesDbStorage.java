package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public void addLikeToFilm(long filmId, long userId) {
        if (checkLikeForFilm(filmId, userId)) {
            String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    @Override
    public void deleteLikeFromFilm(long filmId, long userId) {
        if (checkLikeForFilm(filmId, userId)) {
            String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден.");
        }
    }

    @Override
    public List<Long> getLikesForFilm(long filmId) {
        String sql = "SELECT * FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUserId, filmId);
    }

    private Long mapRowToUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }

    private boolean checkLikeForFilm(long filmId, long userId) {
        if (userDbStorage.checkIfUserIsInDb(userId) && filmDbStorage.checkIfFilmIsInDb(filmId)) {
            return true;
        } else {
            throw new NotFoundException("Фильм " + filmId + " и/или пользователь " + userId + " не найден.");
        }
    }
}
