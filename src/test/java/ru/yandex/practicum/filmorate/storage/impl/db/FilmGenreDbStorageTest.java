package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
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
class FilmGenreDbStorageTest {
    @Autowired
    private FilmGenreDbStorage filmGenreStorage;

    @Test
    void findFilmGenreByFilmIds() {
        List<FilmGenre> filmGenreByFilmIds = filmGenreStorage.findFilmGenreByFilmIds(List.of(2L));
        assertThat(filmGenreByFilmIds.size()).isEqualTo(2);
        assertThat(filmGenreByFilmIds.get(1).getGenreId()).isEqualTo(3);
    }

    @Test
    void insertIntoFilmGenres() {
        List<Genre> genres = List.of(new Genre(4, "Триллер"), new Genre(1, "Комедия"));
        Film film = new Film(1L, "test1", "test1", LocalDate.of(1994, 8, 6), 100, new RatingMpa(1, EnumMPA.G), genres);
        filmGenreStorage.insertIntoFilmGenres(film);
        List<FilmGenre> filmGenreByFilmIds = filmGenreStorage.findFilmGenreByFilmIds(List.of(1L));
        assertThat(filmGenreByFilmIds.size()).isEqualTo(3);
        assertThat(filmGenreByFilmIds.get(0).getGenreId()).isEqualTo(1);
    }

    @Test
    void updateFilmGenres() {
        List<Genre> genres = List.of(new Genre(3, "Мультфильм"));
        Film updated = new Film(1L, "update", "test1", LocalDate.of(1994, 9, 6), 100, new RatingMpa(1, EnumMPA.G), genres);
        filmGenreStorage.updateFilmGenres(updated);
        List<FilmGenre> filmGenreByFilmIds = filmGenreStorage.findFilmGenreByFilmIds(List.of(1L));
        assertThat(filmGenreByFilmIds.size()).isEqualTo(1);
        assertThat(filmGenreByFilmIds.get(0).getGenreId()).isEqualTo(3);
    }
}