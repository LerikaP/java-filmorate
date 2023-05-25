package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int userIdGenerator = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя {}", user);
        long id = getNextFreeId();
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление данных пользователя {}", user);
        long id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
        } else {
            log.warn("Пользователь для обновления данных не найден {}", user);
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        log.info("Получение пользователя по id {}", id);
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Пользователь не найден {}", id);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private int getNextFreeId() {
        return userIdGenerator++;
    }
}
