package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.enums.EnumMPA;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Test
    void save() {
        List<Genre> genres = List.of(new Genre(1, "Комедия"));
        Film film = new Film(
                null, "test4", "test4", LocalDate.of(1998, 10, 14), 90, new RatingMpa(3, EnumMPA.PG_13), genres);
        Film saved = filmStorage.save(film);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isEqualTo(4L);
        assertThat(saved.getMpa()).isEqualTo(new RatingMpa(3, EnumMPA.PG_13));
        assertThat(saved.getGenres().size()).isEqualTo(1);
    }

    @Test
    void update() {
        List<Genre> genres = List.of(new Genre(3, "Мультфильм"));
        Film updated = new Film(1L, "update", "test1", LocalDate.of(1994, 9, 6), 100, new RatingMpa(1, EnumMPA.G), genres);
        filmStorage.update(updated);
        Film afterUpdate = filmStorage.findFilmById(1L);
        assertThat(afterUpdate).isEqualTo(updated);
        assertThat(afterUpdate.getGenres().get(0)).isEqualTo(new Genre(3, "Мультфильм"));
        assertThat(afterUpdate.getName()).isEqualTo("update");
    }

    @Test
    void findAll() {
    }

    @Test
    void findFilmById() {
    }
}