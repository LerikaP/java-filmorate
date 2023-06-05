package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikesStorage {

    void addLikeToFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);

    List<Long> getLikesForFilm(long filmId);
}
