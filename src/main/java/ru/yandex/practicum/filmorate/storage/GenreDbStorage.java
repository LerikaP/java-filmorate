package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(int id) {
        log.info("Получение жанра по id {}", id);
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql, id);
        if (srs.next()) {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
        } else {
            log.warn("Жанр не найден {}", id);
            throw new NotFoundException("Жанр не найден");
        }
    }

    @Override
    public void addGenresToFilmInDb(long filmId, int genreId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    public Set<Genre> getGenresForFilmFromDb(long filmId) {
        Set<Genre> genres = new LinkedHashSet<>();
        List<Integer> genresIds = new ArrayList<>();
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql, filmId);
        while (srs.next()) {
            genresIds.add(srs.getInt("genre_id"));
        }
        sql = "SELECT * FROM genres WHERE genre_id = ?";
        Collections.reverse(genresIds);
        for (int genreId : genresIds) {
            genres.add(jdbcTemplate.queryForObject(sql, this::mapRowToGenre, genreId));
        }
        return genres;
    }

    public void deleteFilmGenres(long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
