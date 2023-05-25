package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private int filmIdGenerator = 1;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление фильма {}", film);
        long id = getNextFreeId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление данных фильма {}", film);
        long id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
        } else {
            log.warn("Фильм для обновления данных не найден {}", film);
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        log.info("Получение фильма по id {}", id);
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.warn("Фильм не найден {}", id);
            throw new NotFoundException("Фильм не найден");
        }
    }

    private int getNextFreeId() {
        return filmIdGenerator++;
    }
}
