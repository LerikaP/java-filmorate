package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        if (checkIfUserExists(userId) && checkIfUserExists(friendId)) {
            friendsStorage.addFriend(userId, friendId);
        }
    }

    public void deleteFriend(long userId, long friendId) {
        if (checkIfUserExists(userId) && checkIfUserExists(friendId) && checkForFriendship(userId, friendId)) {
            friendsStorage.deleteFriend(userId, friendId);
        }
    }

    public List<User> getFriends(long userId) {
        List<User> friends = new ArrayList<>();
        for (Long id : friendsStorage.getFriendsIds(userId)) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return getFriends(userId).stream()
                .filter(x -> getFriends(otherUserId).contains(x))
                .collect(Collectors.toList());
    }

    private boolean checkIfUserExists(long id) {
        User user = userStorage.getUserById(id);
        if (user != null) {
            return true;
        } else {
            throw new NotFoundException("Пользователь " + id + " не найден.");
        }
    }

    private boolean checkForFriendship(long userId, long friendId) {
        List<Long> userFriendsIds = friendsStorage.getFriendsIds(userId);
        if (!userFriendsIds.contains(friendId)) {
            throw new NotFoundException("Пользователь " + friendId + " не найден в друзьях " +
                    "у пользователя " + userId + ".");
        }
        return true;
    }
}
