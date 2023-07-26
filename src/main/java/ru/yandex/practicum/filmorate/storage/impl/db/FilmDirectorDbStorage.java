package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public FilmDirectorDbStorage(NamedParameterJdbcOperations jdbcOperations, JdbcTemplate jdbcTemplate) {
        this.jdbcOperations = jdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmDirector> findFilmDirectorByFilmIds(List<Long> filmIds) {
        return null;
    }

    @Override
    public void insertIntoFilmDirectors(Film film) {
        if (Objects.nonNull(film.getDirectors())) {

            List<Long> directorsIds = film.getDirectors().stream()
                    .map(Director::getId).distinct().collect(Collectors.toList());
            String sqlQueryFilmGenre = "insert into \"film_directors\" (film_id, director_id) values (?, ?)";
            jdbcTemplate.batchUpdate(
                    sqlQueryFilmGenre,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, directorsIds.get(i));
                        }

                        public int getBatchSize() {
                            return directorsIds.size();
                        }
                    });
        }
    }

    @Override
    public void updateFilmDirectors(Film film) {
        String sqlQueryDeleteFilmGenre = "delete from \"film_director\" where film_id = :film_id";
        KeyHolder keyHolderDeleteFilmGenre = new GeneratedKeyHolder();
        MapSqlParameterSource mapDeleteFilmDirector = new MapSqlParameterSource();
        mapDeleteFilmDirector.addValue("film_id", film.getId());
        jdbcOperations.update(sqlQueryDeleteFilmGenre, mapDeleteFilmDirector, keyHolderDeleteFilmGenre);
        insertIntoFilmDirectors(film);
    }

    private static class FilmeDirectorRowMapper implements RowMapper<FilmDirector> {
        @Override
        public FilmDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmDirector(
                    rs.getLong("film_director_id"),
                    rs.getLong("film_id"),
                    rs.getLong("director_id")
            );
        }
    }
}
