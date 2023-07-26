package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User save(User user);

    void delete(Long userId);

    User update(User user);

    List<User> findAll();

    User findUserById(Long userId);

    List<User> findUsersByIds(List<Long> friendsId);
}
