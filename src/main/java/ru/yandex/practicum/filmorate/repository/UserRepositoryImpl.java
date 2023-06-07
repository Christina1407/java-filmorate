package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;
    @Override
    public User save(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException();
        }
        user.setId(++id);
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
          user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if(Objects.nonNull(user.getId())) {
            if (!users.containsKey(user.getId())) {
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
}
