package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public GenreDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public List<Genre> findAll() {
        String sqlQuery = "select * from \"genre\" ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new GenreRowMapper());
    }


    @Override
    public Genre findGenreById(int genreId) {
        String sqlQuery = "select * from \"genre\" where genre_id = :genre_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("genre_id", genreId);
        List<Genre> genre = jdbcOperations.query(sqlQuery, map, new GenreRowMapper());
        if (genre.size() != 1) {
            return null;
        } else {
            return genre.get(0);
        }
    }

    @Override
    public List<Genre> findGenreByIds(List<Integer> genreIds) {
        String sqlQuery = "select * from \"genre\" where genre_id in (:genreIds)  ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("genreIds", genreIds);

        return jdbcOperations.query(sqlQuery, map, new GenreRowMapper());
    }

    static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("name")
            );
        }
    }
}
