package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private final UserStorage userStorage;
    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Boolean addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(friendId);
        if (Objects.nonNull(user) && Objects.nonNull(userFriend)) {
            user.addFriend(friendId);
            return userFriend.addFriend(userId);
        } else return false;

    }

    @Override
    public Boolean deleteFriend(Long userId, Long friendId) {
        userStorage.findUserById(userId).deleteFriend(friendId);
        return userStorage.findUserById(friendId).deleteFriend(userId);
    }

    @Override
    public Set<Long> findMutualFriends(Long userId, Long friendId) {
        Set<Long> mutualFriends = new HashSet<>(userStorage.findUserById(userId).getFriendsId());
        mutualFriends.retainAll(userStorage.findUserById(friendId).getFriendsId());
        return mutualFriends;
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
        return userStorage.findUserById(userId);
    }
}
