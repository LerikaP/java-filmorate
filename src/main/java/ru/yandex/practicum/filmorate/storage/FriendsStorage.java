package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FriendsStorage {

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<Long> getFriendsIds(long userId);
}
