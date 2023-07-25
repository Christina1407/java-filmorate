package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.enums.EnumMPA;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingDbStorageTest {
    @Autowired
    private RatingDbStorage ratingStorage;

    @Test
    void findAll() {
        List<RatingMpa> ratingMpaList = ratingStorage.findAll();
        assertThat(ratingMpaList.size()).isEqualTo(5);
        assertThat(ratingMpaList.get(4).getName()).isEqualTo(EnumMPA.NC_17);
    }

    @Test
    void findRatingById() {
        Optional<RatingMpa> ratingOptional = Optional.ofNullable(ratingStorage.findRatingById(1));
        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", EnumMPA.G)
                );
    }
}