package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        log.info("Добавлен новый пользователь: {}", savedUser);
        return savedUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User updateUser = userRepository.update(user);
        log.info("Пользователь обновлён : {}", updateUser);
        return updateUser;
    }
}
