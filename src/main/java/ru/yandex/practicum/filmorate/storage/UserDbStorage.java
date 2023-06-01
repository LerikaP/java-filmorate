package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя {}", user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление данных пользователя {}", user);
        long userId = user.getId();
        if (checkIfUserIsInDb(userId)) {
            String sqlForUser = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
            jdbcTemplate.update(sqlForUser, user.getLogin(), user.getName(), user.getEmail(),
                    user.getBirthday(), user.getId());
        } else {
            log.warn("Пользователь не найден {}", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        return getUserById(userId);
    }

    @Override
    public User getUserById(long id) {
        log.info("Получение пользователя по id {}", id);
        if (checkIfUserIsInDb(id)) {
            String sql = "SELECT * FROM users WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } else {
            log.warn("Пользователь не найден {}", id);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public boolean checkIfUserIsInDb(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql, id);
        return srs.next();
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
        if (name == null || name.isBlank()) {
            user.setName(login);
        }
        return user;
    }
}
