package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

import java.util.List;

@Repository
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private static final int LIKE = 1;
    private static final int DISLIKE = -1;
    private final NamedParameterJdbcOperations jdbcOperations;

    public ReviewLikeDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public void addLikeDislike(Long reviewId, Long userId, boolean isLike) {
        String sqlQuery = "insert into \"review_like\" (review_id, user_id, like_dislike) values (:review_id, :user_id, :like_dislike)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        if (isLike) {
            map.addValue("like_dislike", LIKE);
        } else {
            map.addValue("like_dislike", DISLIKE);
        }

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public void deleteLikeDislike(Long reviewId, Long userId, boolean isLike) {
        String sqlQuery = "delete from \"review_like\" where user_id = :user_id and review_id = :review_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public Integer sumLikeDislike(Long reviewId) {
        String sqlQuery = "select sum(like_dislike) as useful from \"review_like\" where review_id = :review_id";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        return jdbcOperations.queryForObject(sqlQuery, map, Integer.class);
    }

    @Override
    public List<Long> whoLikeReview(Long reviewId) {
        String sqlQuery = "select user_id from \"review_like\" where review_id = :review_id and like_dislike = :LIKE";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("LIKE", LIKE);
        return jdbcOperations.query(sqlQuery, map, (rs, rowNum) -> rs.getLong("user_id"));
    }

    @Override
    public List<Long> whoDislikeReview(Long reviewId) {
        String sqlQuery = "select user_id from \"review_like\" where review_id = :review_id and like_dislike = :DISLIKE";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("DISLIKE", DISLIKE);
        return jdbcOperations.query(sqlQuery, map, (rs, rowNum) -> rs.getLong("user_id"));
    }

}
