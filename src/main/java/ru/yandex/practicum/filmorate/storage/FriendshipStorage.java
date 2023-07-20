package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.EnumRelationType;

import java.util.List;

public interface FriendshipStorage {
    void addFriendship(Long userId, Long friendId, EnumRelationType relationType);

    void updateFriendship(Long userId, Long friendId, EnumRelationType relationType);

    void deleteFriendship(Long userId, Long friendId);
    Friendship findFriendshipByUserIdAndFriendId (Long userId, Long friendId);
    List<Long> findUsersFriendsIds(Long userId);
}
