package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        for (Film film : filmStorage.getAllFilms()) {
            film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
            films.add(film);
        }
        return films;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        for (Genre genre : film.getGenres()) {
            genreStorage.addGenresToFilmInDb(film.getId(), genre.getId());
        }
        film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
        return getFilmById(film.getId());
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        genreStorage.deleteFilmGenres(film.getId());
        for (Genre genre : film.getGenres()) {
            genreStorage.addGenresToFilmInDb(film.getId(), genre.getId());
        }
        film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
        return getFilmById(film.getId());
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        film.setGenres(genreStorage.getGenresForFilmFromDb(film.getId()));
        return film;
    }

    public void addLikeToFilm(long filmId, long userId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);
        if (checkIfFilmAndUserExist(filmId, userId)) {
            likesStorage.addLikeToFilm(filmId, userId);
        }
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);
        if (checkIfFilmAndUserExist(filmId, userId) && checkLikeForFilm(filmId, userId)) {
            likesStorage.deleteLikeFromFilm(filmId, userId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = getAllFilms();
        films.sort(Comparator.comparingInt(film -> likesStorage.getLikesForFilm(film.getId()).size()));
        Collections.reverse(films);
        return films.subList(0, Math.min(films.size(), count));
    }

    private boolean checkIfFilmAndUserExist(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Фильм " + filmId + " и/или пользователь " + userId + " не найден.");
        }
        return true;
    }

    private boolean checkLikeForFilm(long filmId, long userId) {
        List<Long> likes = likesStorage.getLikesForFilm(filmId);
        if (!likes.contains(userId)) {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден.");
        }
        return true;
    }
}
