package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.*;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User save(User user) {
        user.setId(++id);
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (Objects.nonNull(user.getId())) {
            if (!users.containsKey(user.getId())) {
                log.error("Пользователя с таким id не найдено: {}", user.getId());
                throw new ValidationException();
            } else {
                users.put(user.getId(), user);
            }
        } else {
            save(user);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findUsersByIds(Set<Long> friendsId) {
        List<User> userFriends = new ArrayList<>();
        friendsId.forEach(id -> {
            if (users.containsKey(id)) {
                userFriends.add(users.get(id));
            }
        });
        return userFriends;
    }


}
