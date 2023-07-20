package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.EnumRelationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(friendId);
        //проверяем, что пользователи существуют
        if (Objects.nonNull(user) && Objects.nonNull(userFriend)) {
            Friendship friendship = friendshipStorage.findFriendshipByUserIdAndFriendId(userId, friendId);
            //если запись о дружбе уже существует, то выбрасываем исключение
            if (Objects.nonNull(friendship)) {
                if (EnumRelationType.FRIEND.equals(friendship.getRelationType())) {
                    log.error("Пользователи уже друзья");
                    throw new AlreadyExistsException(); // TODO fix me
                }
                log.error("Запрос на добавление в друзья пользователю id = {} уже отправлен", friendId);
                throw new AlreadyExistsException(); // TODO fix me
            }
            Friendship friendshipFriend = friendshipStorage.findFriendshipByUserIdAndFriendId(friendId, userId);
            // проверяем наличие обратной записи в таблице дружба
            if (Objects.nonNull(friendshipFriend)) {
                if (EnumRelationType.FRIEND.equals(friendshipFriend.getRelationType())) {
                    log.error("Пользователи уже друзья");
                    throw new RuntimeException(); // TODO fix me
                }
                //подтверждение дружбы
                friendshipStorage.updateFriendship(friendId, userId, EnumRelationType.FRIEND);
                friendshipStorage.addFriendship(userId, friendId, EnumRelationType.FRIEND);
                //создаем заявку на добавление в друзья
            } else {
                friendshipStorage.addFriendship(userId,friendId, EnumRelationType.NOT_APPROVED_FRIEND);
            }
        } else {
            log.error("Проверьте айди пользователей");
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(friendId);
        if (Objects.nonNull(user) && Objects.nonNull(userFriend)) {
            Friendship friendship = friendshipStorage.findFriendshipByUserIdAndFriendId(userId, friendId);
            if (Objects.nonNull(friendship)) {
                if (EnumRelationType.FRIEND.equals(friendship.getRelationType())) {
                    friendshipStorage.deleteFriendship(userId, friendId);
                    friendshipStorage.updateFriendship(friendId, userId, EnumRelationType.NOT_APPROVED_FRIEND);
                } else if (EnumRelationType.NOT_APPROVED_FRIEND.equals(friendship.getRelationType())) {
                    friendshipStorage.deleteFriendship(userId, friendId);
                } else {
                    log.error("Найден недопустимый тип дружбы");
                    throw new NotFoundException();
                }
            }
        }
    }

    @Override
    public List<User> findMutualFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherId);
        if (Objects.nonNull(user) && Objects.nonNull(otherUser)) {
            Set<Long> userFriends = new HashSet<>(friendshipStorage.findUsersFriendsIds(userId));
            Set<Long> otherFriends = new HashSet<>(friendshipStorage.findUsersFriendsIds(otherId));
            //находим пересечение по айди
            userFriends.retainAll(otherFriends);
            return userStorage.findUsersByIds(new ArrayList<>(userFriends));
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public User saveUser(User user) {
        return userStorage.save(user);
    }

    @Override
    public User updateUser(User user) {
        User updateUser = userStorage.update(user);
        if (Objects.isNull(updateUser)) {
            log.error("Пользователь для обновления не найден");
            throw new NotFoundException();
        }
        return updateUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        if (Objects.nonNull(userStorage.findUserById(userId))) {
            return userStorage.findUserById(userId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public List<User> findUsersFriends(Long userId) {
        User user = userStorage.findUserById(userId);
        if (Objects.nonNull(user)) {
            return userStorage.findUsersByIds(friendshipStorage.findUsersFriendsIds(userId));

        } else {
            throw new NotFoundException();
        }
    }
}
