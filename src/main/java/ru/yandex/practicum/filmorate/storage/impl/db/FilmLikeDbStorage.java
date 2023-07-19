package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationType;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Long> whoLikeFilm(Long filmId) {
        String sqlQuery = "select * from \"film_like\" where film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);


        List<FilmLike> filmLikeList = jdbcOperations.query(sqlQuery, map, new FilmLikeRowMapper());
        return filmLikeList.stream()
                .map(FilmLike::getUserId)
                .collect(Collectors.toList());
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
