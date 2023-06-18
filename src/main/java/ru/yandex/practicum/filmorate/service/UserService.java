package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    Boolean addFriend(Long userId, Long friendId);
    Boolean deleteFriend(Long userId, Long friendId);
    Set<Long> findMutualFriends(Long userId, Long friendId);
    User saveUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User findUserById(Long userId);
}
