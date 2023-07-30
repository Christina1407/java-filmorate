package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.enums.EnumMPA;
import ru.yandex.practicum.filmorate.model.enums.EnumSortBy;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmDbStorage(NamedParameterJdbcOperations jdbcOperations, JdbcTemplate jdbcTemplate) {
        this.jdbcOperations = jdbcOperations;
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
        return film;
    }

    @Override
    public void delete(Long filmId) {
        String sqlQuery = "delete from \"film\" where film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
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
        if (Objects.nonNull(film.getMpa())) {
            map.addValue("rating_id", film.getMpa().getId());
        } else {
            map.addValue("rating_id", null);
        }

        jdbcOperations.update(sqlQuery, map, keyHolder);
        if (Objects.isNull(keyHolder.getKey())) {
            return null;
        }
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
        String sqlQuery = "select f.*, r.name as rating_mpa_name from \"film\" as f left join \"rating_mpa\" as r on f.rating_id = r.rating_id" +
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

    @Override
    public List<Film> searchFilms(String query, List<String> searchByParams) {
        List<Film> films = new ArrayList<>();
        if (searchByParams.contains("title") && searchByParams.contains("director")) {
            films = searchFilmsByTitleAndDirector(query);
        }
        return films;
    }

    private List<Film> searchFilmsByTitleAndDirector(String query) {
        String sqlQuery = "select f.*, r.name as rating_mpa_name from \"film\" as f " +
                "left join \"film_director\" as fd on f.film_id = fd.film_id " +
                "left join \"rating_mpa\" as r on f.rating_id = r.rating_id " +
                "left join \"director\" as d on fd.director_id = d.director_id " +
                "left join \"film_like\" as fl on f.film_id = fl.film_id " +
                "where f.name ilike :query or d.name ilike :query " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) desc";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("query", "%" + query + "%");
        return jdbcOperations.query(sqlQuery, map, new FilmRowMapper());
    }

    @Override
    public List<Film> findFilmsByDirector(Long directorId, EnumSortBy sortBy) {
        String sqlQuery = null;
        if (Objects.isNull(sortBy)) {
            sqlQuery = "select f.*, r.name as rating_mpa_name from \"film\" as f left join \"film_director\" as fd on f.film_id = fd.film_id" +
                    " left join \"rating_mpa\" as r on f.rating_id = r.rating_id where director_id = :director_id";
        } else if (sortBy.equals(EnumSortBy.YEAR)) {
            sqlQuery = "select f.*, r.name as rating_mpa_name " +
                    "from \"film\"  as f left join \"film_director\" as fd on f.film_id = fd.film_id " +
                    "left join \"rating_mpa\" as r on f.rating_id = r.rating_id " +
                    "where director_id = :director_id " +
                    "ORDER BY EXTRACT(YEAR FROM release_date)";
        } else if (sortBy.equals(EnumSortBy.LIKES)) {
            sqlQuery = "select f.*, r.name as rating_mpa_name " +
                    "from \"film\"  as f  join \"film_director\" as fd on f.film_id = fd.film_id " +
                    "left join \"rating_mpa\" as r on f.rating_id = r.rating_id " +
                    "left join \"film_like\" as fl on f.film_id = fl.film_id " +
                    "where director_id = :director_id " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY COUNT(fl.USER_ID)  desc";
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("director_id", directorId);
        List<Film> films = jdbcOperations.query(sqlQuery, map, new FilmRowMapper());
        return films;
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            RatingMpa ratingMpa = null;
            if (Objects.nonNull(rs.getString("rating_mpa_name"))) {
                ratingMpa = new RatingMpa(rs.getInt("rating_id"), EnumMPA.valueOf(rs.getString("rating_mpa_name")));
            }
            return new Film(
                    rs.getLong("film_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    ratingMpa,
                    null,
                    null
            );
        }
    }
}
