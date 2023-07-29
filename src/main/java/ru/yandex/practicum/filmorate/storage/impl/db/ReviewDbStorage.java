package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public ReviewDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Review> getAllReviews() {
        String sqlQuery = "select * from \"review\" ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcOperations.query(sqlQuery, map, new ReviewRowMapper());
    }

    @Override
    public Review findReviewById(Long reviewId) {
        String sqlQuery = "select * from \"review\" where review_id = :review_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        List<Review> review = jdbcOperations.query(sqlQuery, map, new ReviewRowMapper());
        if (review.size() != 1) {
            return null;
        } else {
            return review.get(0);
        }
    }

    @Override
    public Review saveReview(Review review) {
        String sqlQuery = "insert into \"review\" (film_id, user_id, content, is_positive) " +
                "values (:film_id, :user_id, :content, :is_positive)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        map.addValue("film_id", review.getFilmId());
        map.addValue("user_id", review.getUserId());
        map.addValue("content", review.getContent());
        map.addValue("is_positive", review.getIsPositive());


        jdbcOperations.update(sqlQuery, map, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "update \"review\" set content = " +
                ":content, is_positive = :is_positive where review_id = :review_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();

        map.addValue("review_id", review.getReviewId());
        map.addValue("content", review.getContent());
        map.addValue("is_positive", review.getIsPositive());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        if (Objects.isNull(keyHolder.getKey())) {
            return null;
        }
        return findReviewById(review.getReviewId());

    }

    @Override
    public void deleteReview(Long reviewId) {
        String sqlQuery = "delete from \"review\" where review_id = :review_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    @Override
    public List<Review> findReviewsByFilmId(Long filmId) {
        String sqlQuery = "select * from \"review\" where film_id = :film_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("film_id", filmId);
        return jdbcOperations.query(sqlQuery, map, new ReviewRowMapper());

    }

    static class ReviewRowMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Review(
                    rs.getLong("review_id"),
                    rs.getLong("user_id"),
                    rs.getLong("film_id"),
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    0
            );
        }
    }
}
