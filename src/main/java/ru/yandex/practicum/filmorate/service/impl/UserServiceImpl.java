package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.model.enums.EnumRelationType;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.feedStorage = feedStorage;
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
                    throw new AlreadyExistsException();
                }
                log.error("Запрос на добавление в друзья пользователю id = {} уже отправлен", friendId);
                throw new AlreadyExistsException();
            }
            Friendship friendshipFriend = friendshipStorage.findFriendshipByUserIdAndFriendId(friendId, userId);
            // проверяем наличие обратной записи в таблице дружба
            if (Objects.nonNull(friendshipFriend)) {
                if (EnumRelationType.FRIEND.equals(friendshipFriend.getRelationType())) {
                    log.error("Пользователи уже друзья");
                    throw new RuntimeException();
                }
                //подтверждение дружбы
                friendshipStorage.updateFriendship(friendId, userId, EnumRelationType.FRIEND);
                friendshipStorage.addFriendship(userId, friendId, EnumRelationType.FRIEND);
                feedStorage.save(Feed.builder()
                        .userId(userId)
                        .entityId(friendId)
                        .eventType(EnumEventType.FRIEND)
                        .operation(EnumOperation.ADD)
                        .timestamp(Instant.now().toEpochMilli())
                        .build());
                //создаем заявку на добавление в друзья
            } else {
                friendshipStorage.addFriendship(userId, friendId, EnumRelationType.NOT_APPROVED_FRIEND);
                feedStorage.save(Feed.builder()
                        .userId(userId)
                        .entityId(friendId)
                        .eventType(EnumEventType.FRIEND)
                        .operation(EnumOperation.ADD)
                        .timestamp(Instant.now().toEpochMilli())
                        .build());
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
                    feedStorage.save(Feed.builder()
                            .userId(userId)
                            .entityId(friendId)
                            .eventType(EnumEventType.FRIEND)
                            .operation(EnumOperation.REMOVE)
                            .timestamp(Instant.now().toEpochMilli())
                            .build());
                    friendshipStorage.updateFriendship(friendId, userId, EnumRelationType.NOT_APPROVED_FRIEND);
                } else if (EnumRelationType.NOT_APPROVED_FRIEND.equals(friendship.getRelationType())) {
                    friendshipStorage.deleteFriendship(userId, friendId);
                    feedStorage.save(Feed.builder()
                            .userId(userId)
                            .entityId(friendId)
                            .eventType(EnumEventType.FRIEND)
                            .operation(EnumOperation.REMOVE)
                            .timestamp(Instant.now().toEpochMilli())
                            .build());
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
    public void deleteUser(Long userId) {
        findUserById(userId);
        userStorage.delete(userId);
    }

    @Override
    public User updateUser(User user) {
        findUserById(user.getId());
        return userStorage.update(user);
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
            log.error("Пользователь не найден id {} ", userId);
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

    @Override
    public List<Feed> getFeed(Long userId) {
        findUserById(userId);
        return feedStorage.findUsersFeed(userId);
    }

}
