package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> findMutualFriends(Long userId, Long otherId);

    User saveUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User findUserById(Long userId);

    List<User> findUsersFriends(Long userId);
}
