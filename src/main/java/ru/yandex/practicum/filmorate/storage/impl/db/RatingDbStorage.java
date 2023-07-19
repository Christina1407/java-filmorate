package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.EnumMPA;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class RatingDbStorage implements RatingStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public RatingDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<RatingMpa> findAll() {
        String sqlQuery = "select * from \"rating_mpa\" ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new RatingRowMapper());
    }

    @Override
    public RatingMpa findRatingById(int ratingId) {
        String sqlQuery = "select * from \"rating_mpa\" where rating_id = :rating_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("rating_id", ratingId);
        List<RatingMpa> ratingMpa = jdbcOperations.query(sqlQuery, map, new RatingRowMapper());
        if (ratingMpa.size() != 1) {
            return null;
        } else {
            return ratingMpa.get(0);
        }
    }

    private static class RatingRowMapper implements RowMapper<RatingMpa> {
        @Override
        public RatingMpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RatingMpa(
                    rs.getInt("rating_id"),
                    EnumMPA.valueOf(rs.getString("name"))
            );
        }
    }
}
