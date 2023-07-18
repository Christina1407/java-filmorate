package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationType;

import java.util.List;

public interface FriendshipStorage {
    void addFriendship(Long userId, Long friendId, RelationType relationType);

    void updateFriendship(Long userId, Long friendId, RelationType relationType);

    void deleteFriendship(Long userId, Long friendId);
    Friendship findFriendshipByUserIdAndFriendId (Long userId, Long friendId);
    List<Long> findUsersFriendsIds(Long userId);
}
