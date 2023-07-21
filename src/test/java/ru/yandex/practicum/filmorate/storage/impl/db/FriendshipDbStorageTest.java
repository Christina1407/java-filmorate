package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.enums.EnumRelationType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendshipDbStorageTest {
    @Autowired
    private FriendshipDbStorage friendshipStorage;

    @Test
    void addFriendship() {
        friendshipStorage.addFriendship(3L, 1L, EnumRelationType.NOT_APPROVED_FRIEND);
        Friendship friendship13 = friendshipStorage.findFriendshipByUserIdAndFriendId(1L, 3L);
        assertThat(friendship13).isNull();
        Friendship friendship31 = friendshipStorage.findFriendshipByUserIdAndFriendId(3L, 1L);
        assertThat(friendship31).isNotNull();
        assertThat(friendship31.getRelationType()).isEqualTo(EnumRelationType.NOT_APPROVED_FRIEND);
    }

    @Test
    void updateFriendship() {
        friendshipStorage.updateFriendship(3L, 2L, EnumRelationType.FRIEND);
        Friendship friendship32 = friendshipStorage.findFriendshipByUserIdAndFriendId(3L, 2L);
        assertThat(friendship32).isNotNull();
        assertThat(friendship32.getRelationType()).isEqualTo(EnumRelationType.FRIEND);
    }

    @Test
    void deleteFriendship() {
        friendshipStorage.deleteFriendship(3L, 2L);
        Friendship friendship32 = friendshipStorage.findFriendshipByUserIdAndFriendId(3L, 2L);
        assertThat(friendship32).isNull();
    }

    @Test
    void findFriendshipByUserIdAndFriendId() {
        Friendship friendship = friendshipStorage.findFriendshipByUserIdAndFriendId(1L, 2L);
        assertThat(friendship).isNotNull();
        assertThat(friendship.getRelationType()).isEqualTo(EnumRelationType.FRIEND);
    }

    @Test
    void findUsersFriendsIds() {
        List<Long> usersFriendsIds = friendshipStorage.findUsersFriendsIds(1L);
        assertThat(usersFriendsIds.size()).isEqualTo(1);
        List<Long> usersFriendsIds3 = friendshipStorage.findUsersFriendsIds(3L);
        assertThat(usersFriendsIds3.size()).isEqualTo(1);

    }
}