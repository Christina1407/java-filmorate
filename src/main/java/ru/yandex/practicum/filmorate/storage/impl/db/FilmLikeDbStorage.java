package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmLikeDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        String sqlQuery = "insert into \"film_like\" (film_id, user_id) values (:film_id, :user_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);
        map.addValue("user_id", userId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        String sqlQuery = "delete from \"film_like\" where user_id = :user_id and film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        map.addValue("film_id", filmId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public List<Long> usersIdsWhoLikeFilm(Long filmId) { //Лист userId
        String sqlQuery = "select user_id from \"film_like\" where film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);

        return jdbcOperations.query(sqlQuery, map, (rs, rowNum) -> rs.getLong("user_id"));

//        List<FilmLike> filmLikeList = jdbcOperations.query(sqlQuery, map, new FilmLikeRowMapper());
//        return filmLikeList.stream()
//                .map(FilmLike::getUserId)
//                .collect(Collectors.toList());
    }

    @Override
    public List<FilmLike> userFilmLike(Long userId) {

        String sqlQuery = "select * from \"film_like\" where user_id = :user_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);

        return jdbcOperations.query(sqlQuery, map, new FilmLikeRowMapper());
    }

    @Override
    public List<FilmLike> filmLikesUsersWithCommonLike(Long userId) {

        String sqlQuery = "select * from \"film_like\" where user_id != :user_id and user_id in " +
                "(select user_id from \"film_like\" where film_id " +
                "in (select film_id from \"film_like\" where user_id = :user_id))";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);

        return jdbcOperations.query(sqlQuery, map, new FilmLikeRowMapper());
    }


    private static class FilmLikeRowMapper implements RowMapper<FilmLike> {
        @Override
        public FilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmLike(
                    rs.getLong("film_like_id"),
                    rs.getLong("film_id"),
                    rs.getLong("user_id")
            );
        }
    }
}
