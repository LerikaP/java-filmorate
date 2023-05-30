package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление фильма {}", film);
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("ID");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        for (Genre genre : film.getGenres()) {
            genreStorage.addGenresToFilmInDb(film.getId(), genre.getId());
        }
        film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление данных фильма {}", film);
        long filmId = film.getId();
        if (checkIfFilmIsInDb(filmId)) {
            String sqlForFilmUpdate = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                    "duration = ?, mpa_id = ? WHERE id = ?";
            jdbcTemplate.update(sqlForFilmUpdate, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(), film.getId());
            genreStorage.deleteFilmGenres(filmId);
            for (Genre genre : film.getGenres()) {
                genreStorage.addGenresToFilmInDb(film.getId(), genre.getId());
            }
            film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
        } else {
            log.warn("Фильм для обновления данных не найден {}", filmId);
            throw new NotFoundException("Фильм не найден");
        }
        return getFilmById(filmId);
    }

    @Override
    public Film getFilmById(long id) {
        log.info("Получение фильма по id {}", id);
        if (checkIfFilmIsInDb(id)) {
            String sql = "SELECT * FROM films WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } else {
            log.warn("Фильм не найден {}", id);
            throw new NotFoundException("Фильм не найден");
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Mpa mpa = mpaStorage.getMpaById(rs.getInt("mpa_id"));
        Set<Genre> genres = genreStorage.getGenresForFilmFromDb(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    protected boolean checkIfFilmIsInDb(long id) {
        String sqlForCheck = "SELECT * FROM films WHERE id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlForCheck, id);
        return srs.next();
    }
}
