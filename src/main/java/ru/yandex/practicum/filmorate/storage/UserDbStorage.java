package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationType;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public UserDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public User save(User user) {
        String sqlQuery = "insert into \"user\" (email, login, name, birthday) values (:email, :login, :name, :birthday)";
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update \"user\" set email = :email, login = :login, name = :name, birthday = :birthday " +
                "where user_id = :user_id ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", user.getId());
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());

        jdbcOperations.update(sqlQuery, map, keyHolder);
       if (Objects.isNull(keyHolder.getKey())) {
           return null;
       }
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from \"user\" ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new UserRowMapper());

    }

    @Override
    public User findUserById(Long userId) {
        String sqlQuery = "select * from \"user\" where user_id = :user_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        List<User> user = jdbcOperations.query(sqlQuery, map, new UserRowMapper());
        if (user.size() != 1) {
            return null;
        } else {
            return user.get(0);
        }

    }

    @Override
    public List<User> findUsersByIds(List<Long> friendsId) {
        String sqlQuery = "select * from \"user\" where user_id in (:friendsId)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("friendsId", friendsId);

        return jdbcOperations.query(sqlQuery, map, new UserRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }
    }
}
