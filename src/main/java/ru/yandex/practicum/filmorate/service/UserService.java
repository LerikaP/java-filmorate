package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friendUser.getFriends().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendId);
        Set<Long> userFriendIds = user.getFriends();
        Set<Long> friendUserFriendIds = friendUser.getFriends();
        if (user.getFriends().contains(friendId)) {
            userFriendIds.remove(friendId);
        } else {
            throw new NotFoundException("Пользователь " + friendUser + " не найден в друзьях " + user);
        }
        if (friendUserFriendIds.contains(userId)) {
            friendUserFriendIds.remove(userId);
        } else {
            throw new NotFoundException("Пользователь " + user + " не найден в друзьях " + friendUser);
        }
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        return commonFriends.stream().map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
