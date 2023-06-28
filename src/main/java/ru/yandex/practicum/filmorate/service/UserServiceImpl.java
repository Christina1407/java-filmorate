package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(friendId);
        if (Objects.nonNull(user) && Objects.nonNull(userFriend)) {
            if (user.addFriend(friendId)) {
                userFriend.addFriend(userId);
            } else {
                throw new AlreadyExistsException();
            }

        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(friendId);
        if (Objects.nonNull(user) && Objects.nonNull(userFriend)) {
            user.deleteFriend(friendId);
            userFriend.deleteFriend(userId);
        }
    }

    @Override
    public List<User> findMutualFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherId);
        if (Objects.nonNull(user) && Objects.nonNull(otherUser)) {
            Set<Long> mutualFriends = new HashSet<>(userStorage.findUserById(userId).getFriendsId());
            mutualFriends.retainAll(userStorage.findUserById(otherId).getFriendsId());
            return userStorage.findUsersByIds(mutualFriends);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public User saveUser(User user) {
        return userStorage.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        if (Objects.nonNull(userStorage.findUserById(userId))) {
            return userStorage.findUserById(userId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public List<User> findUsersFriends(Long userId) {
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(user)) {
            return userStorage.findUsersByIds(user.getFriendsId());
        } else {
            throw new NotFoundException();
        }
    }
}
