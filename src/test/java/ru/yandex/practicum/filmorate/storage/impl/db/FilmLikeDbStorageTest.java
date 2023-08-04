package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmLikeDbStorageTest {
    @Autowired
    private FilmLikeDbStorage filmLikeStorage;

    @Test
    void addLike() {
        filmLikeStorage.addLike(1L, 3L);
        List<Long> whoLikeFilm = filmLikeStorage.usersIdsWhoLikeFilm(3L);
        assertThat(whoLikeFilm.size()).isEqualTo(1);
        assertThat(whoLikeFilm.get(0)).isEqualTo(1L);
    }

    @Test
    void deleteLike() {
        filmLikeStorage.deleteLike(1L, 1L);
        List<Long> whoLikeFilm = filmLikeStorage.usersIdsWhoLikeFilm(1L);
        assertThat(whoLikeFilm.size()).isEqualTo(1);
        assertThat(whoLikeFilm.get(0)).isEqualTo(2L);
    }

    @Test
    void whoLikeFilm() {
        List<Long> whoLikeFilm = filmLikeStorage.usersIdsWhoLikeFilm(1L);
        assertThat(whoLikeFilm.size()).isEqualTo(2);
        assertThat(whoLikeFilm.get(0)).isEqualTo(2L);
        List<Long> whoLikeFilm3 = filmLikeStorage.usersIdsWhoLikeFilm(3L);
        assertThat(whoLikeFilm3.size()).isEqualTo(0);
    }
}