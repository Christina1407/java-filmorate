package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(NamedParameterJdbcOperations jdbcOperations, JdbcTemplate jdbcTemplate) {
        this.jdbcOperations = jdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmGenre> findFilmGenreByFilmIds(List<Long> filmIds) {
        String sqlQuery = "select * from \"film_genre\" where film_id in (:filmIds)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("filmIds", filmIds);

        return jdbcOperations.query(sqlQuery, map, new FilmeGenreRowMapper());
    }

    private static class FilmeGenreRowMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenre(
                    rs.getLong("film_genre_id"),
                    rs.getLong("film_id"),
                    rs.getInt("genre_id")
            );
        }
    }

    @Override
    public void insertIntoFilmGenres(Film film) {
        if (Objects.nonNull(film.getGenres())) {

            List<Integer> integerSet = film.getGenres().stream()
                    .map(Genre::getId).distinct().collect(Collectors.toList());
            String sqlQueryFilmGenre = "insert into \"film_genre\" (film_id, genre_id) values (?, ?)";
            jdbcTemplate.batchUpdate(
                    sqlQueryFilmGenre,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setInt(2, integerSet.get(i));
                        }

                        public int getBatchSize() {
                            return integerSet.size();
                        }
                    });
        }
    }

    @Override
    public void updateFilmGenres(Film film) {
        String sqlQueryDeleteFilmGenre = "delete from \"film_genre\" where film_id = :film_id";
        KeyHolder keyHolderDeleteFilmGenre = new GeneratedKeyHolder();
        MapSqlParameterSource mapDeleteFilmGenre = new MapSqlParameterSource();
        mapDeleteFilmGenre.addValue("film_id", film.getId());
        jdbcOperations.update(sqlQueryDeleteFilmGenre, mapDeleteFilmGenre, keyHolderDeleteFilmGenre);
        insertIntoFilmGenres(film);
    }
}
