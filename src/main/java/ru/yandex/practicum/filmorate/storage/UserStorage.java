package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> findAll();

    User findUserById(Long userId);

    List<User> findUsersByIds(Set<Long> friendsId);
}
