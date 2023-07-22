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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.enums.EnumMPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(NamedParameterJdbcOperations jdbcOperations, JdbcTemplate jdbcTemplate) {
        this.jdbcOperations = jdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "insert into \"film\" (name, description, release_date, duration, rating_id) " +
                "values (:name, :description, :release_date, :duration, :rating_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("release_date", film.getReleaseDate());
        map.addValue("duration", film.getDuration());
        map.addValue("rating_id", film.getMpa().getId());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        insertIntoFilmGenres(film);
        return film;
    }

    private void insertIntoFilmGenres(Film film) {
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

    private void updateFilmGenres(Film film) {
        String sqlQueryDeleteFilmGenre = "delete from \"film_genre\" where film_id = :film_id";
        KeyHolder keyHolderDeleteFilmGenre = new GeneratedKeyHolder();
        MapSqlParameterSource mapDeleteFilmGenre = new MapSqlParameterSource();
        mapDeleteFilmGenre.addValue("film_id", film.getId());
        jdbcOperations.update(sqlQueryDeleteFilmGenre, mapDeleteFilmGenre, keyHolderDeleteFilmGenre);
        insertIntoFilmGenres(film);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update \"film\" set name = :name, description = :description, release_date = " +
                ":release_date, duration = :duration, rating_id = :rating_id where film_id = :film_id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", film.getId());
        map.addValue("name", film.getName());
        map.addValue("description", film.getDescription());
        map.addValue("release_date", film.getReleaseDate());
        map.addValue("duration", film.getDuration());
        map.addValue("rating_id", film.getMpa().getId());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        if (Objects.isNull(keyHolder.getKey())) {
            return null;
        }
        updateFilmGenres(film);  //при обновлении нужно вносить изменения в film_genre
        return findFilmById(film.getId());
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select f.*, r.name as rating_mpa_name from \"film\" as f left join \"rating_mpa\" as r on f.rating_id = r.rating_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new FilmRowMapper());
    }

    @Override
    public Film findFilmById(Long filmId) {
        String sqlQuery = "select f.*, r.name as rating_mpa_name from \"film\" as f join \"rating_mpa\" as r on f.rating_id = r.rating_id" +
                " where film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);
        List<Film> films = jdbcOperations.query(sqlQuery, map, new FilmRowMapper());
        if (films.size() != 1) {
            return null;
        } else {
            return films.get(0);
        }
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(
                    rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    new RatingMpa(rs.getInt("rating_id"), EnumMPA.valueOf(rs.getString("rating_mpa_name"))),
                    null
            );
        }
    }
}
