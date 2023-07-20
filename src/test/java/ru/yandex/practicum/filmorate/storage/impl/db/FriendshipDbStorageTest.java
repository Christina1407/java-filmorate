package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendshipDbStorageTest {

    @Test
    void addFriendship() {
    }

    @Test
    void updateFriendship() {
    }

    @Test
    void deleteFriendship() {
    }

    @Test
    void findFriendshipByUserIdAndFriendId() {
    }

    @Test
    void findUsersFriendsIds() {
    }
}