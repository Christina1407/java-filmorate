package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void save() {
        User user = new User(
                null, "test@test.ru", "test", "test", LocalDate.of(1994, 1, 1));
        User saved = userStorage.save(user);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isEqualTo(4L);
    }

    @Test
    void update() {
        User updated = new User(1L, "update@test.ru", "update", "test1", LocalDate.of(1980, 1, 10));
        userStorage.update(updated);
        User afterUpdate = userStorage.findUserById(1L);
        assertThat(afterUpdate).isEqualTo(updated);
    }

    @Test
    void findAll() {
        List<User> users = userStorage.findAll();
        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    void findUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(1L));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "test1@test.ru")
                );
    }

    @Test
    void findUsersByIds() {
        List<User> users = userStorage.findUsersByIds(List.of(1L, 3L));
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).getBirthday()).isEqualTo("1994-08-06");
    }
}