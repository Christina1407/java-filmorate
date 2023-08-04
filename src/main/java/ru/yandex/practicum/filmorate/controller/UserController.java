package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;
    FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
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

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        log.info("Пользователь id = : " + userId + " удалён ");
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
        log.info("Пользователю id = " + userId + " добавился в друзья пользователя id = " + friendId);
    }


    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") Long userId,
                             @PathVariable("friendId") Long friendId) {
        userService.deleteFriend(userId, friendId);
        log.info("Пользователь id = " + userId + " и пользователь id = " + friendId + " удалены из друзей");
    }

    @GetMapping("{userId}/friends")
    public List<User> findUsersFriends(@PathVariable("userId") Long userId) {
        return userService.findUsersFriends(userId);
    }

    @GetMapping("{userId}/recommendations")
    public List<Film> findRecommendations(@PathVariable("userId") Long userId) {
        return filmService.findRecommendations(userId);
    }

    @GetMapping("{userId}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("userId") Long userId,
                                        @PathVariable("otherId") Long otherId) {
        return userService.findMutualFriends(userId, otherId);
    }

    @GetMapping("{userId}/feed")
    public List<Feed> getFeed(@PathVariable("userId") Long userId) {
        return userService.getFeed(userId);
    }


}
