package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.RelationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public FriendshipDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public void addFriendship(Long userId, Long friendId, RelationType relationType) {
        String sqlQuery = "insert into \"friendship\" (user_id, friend_id, relation_type) values (:user_id, :friend_id, :relation_type)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        map.addValue("friend_id", friendId);
        map.addValue("relation_type", relationType.name());

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public void updateFriendship(Long userId, Long friendId, RelationType relationType) {
        String sqlQuery = "update \"friendship\" set relation_type = :relation_type where user_id = :user_id and friend_id = :friend_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        map.addValue("friend_id", friendId);
        map.addValue("relation_type", relationType.name());

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public void deleteFriendship(Long userId, Long friendId) {
        String sqlQuery = "delete from \"friendship\" where user_id = :user_id and friend_id = :friend_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        map.addValue("friend_id", friendId);

        jdbcOperations.update(sqlQuery, map, keyHolder);

    }

    @Override
    public Friendship findFriendshipByUserIdAndFriendId(Long userId, Long friendId) {
        String sqlQuery = "select * from \"friendship\" where user_id = :user_id and friend_id = :friend_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);
        map.addValue("friend_id", friendId);

        List<Friendship> friendship = jdbcOperations.query(sqlQuery, map, new FriendshipRowMapper());
        if (friendship.size() != 1) {
            return null;
        } else {
            return friendship.get(0);
        }
    }

    @Override
    public List<Long> findUsersFriendsIds(Long userId) {
        String sqlQuery = "select * from \"friendship\" where user_id = :user_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);

        List<Friendship> friendships = jdbcOperations.query(sqlQuery, map, new FriendshipRowMapper());
        return friendships.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());

    }

    private static class FriendshipRowMapper implements RowMapper<Friendship> {
        @Override
        public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Friendship(
                    rs.getLong("friendship_id"),
                    RelationType.valueOf(rs.getString("relation_type")),
                    rs.getLong("user_id"),
                    rs.getLong("friend_id")
            );
        }
    }
}
