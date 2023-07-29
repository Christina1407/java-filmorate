package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public DirectorDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "select * from \"director\" ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new DirectorRowMapper());
    }

    @Override
    public Director findDirectorById(Long directorId) {
        String sqlQuery = "select * from \"director\" where director_id = :director_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("director_id", directorId);
        List<Director> director = jdbcOperations.query(sqlQuery, map, new DirectorRowMapper());
        if (director.size() != 1) {
            return null;
        } else {
            return director.get(0);
        }
    }


    @Override
    public List<Director> findDirectorsByIds(List<Long> directorsIds) {
        String sqlQuery = "select * from \"director\" where director_id in (:directorsIds)  ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("directorsIds", directorsIds);

        return jdbcOperations.query(sqlQuery, map, new DirectorRowMapper());
    }

    @Override
    public Director save(Director director) {
        String sqlQuery = "insert into \"director\" (name) values (:name)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        map.addValue("name", director.getName());


        jdbcOperations.update(sqlQuery, map, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "update \"director\" set name = :name where director_id = :director_id ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("director_id", director.getId());
        map.addValue("name", director.getName());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        if (Objects.isNull(keyHolder.getKey())) {
            return null;
        }
        return director;
    }

    @Override
    public void delete(Long directorId) {
        String sqlQuery = "delete from \"director\" where director_id = :director_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("director_id", directorId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    static class DirectorRowMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Director(
                    rs.getLong("director_id"),
                    rs.getString("name")
            );
        }
    }
}
