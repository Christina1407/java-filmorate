package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        User savedUser = userService.saveUser(user);
        log.info("Добавлен новый пользователь: {}", savedUser);
        return savedUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User updateUser = userService.updateUser(user);
        log.info("Пользователь обновлён : {}", updateUser);
        return updateUser;
    }

    @GetMapping("{userId}")
    public User findUser(@PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }
    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable("userId") Long userId,
                          @PathVariable("friendId") Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") Long userId,
                             @PathVariable("friendId") Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

}
