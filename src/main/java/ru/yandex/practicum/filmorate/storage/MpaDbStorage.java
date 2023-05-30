package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa getMpaById(int id) {
        log.info("Получение рейтинга по id {}", id);
        String sql = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql, id);
        if (srs.next()) {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
        } else {
            log.warn("Рейтинг не найден {}", id);
            throw new NotFoundException("Рейтинг не найден!");
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
