package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public FilmGenreDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
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
}
