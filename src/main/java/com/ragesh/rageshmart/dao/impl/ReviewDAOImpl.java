package com.ragesh.rageshmart.dao.impl;

import com.ragesh.rageshmart.dao.ReviewDAO;
import com.ragesh.rageshmart.exception.DataAccessException;
import com.ragesh.rageshmart.model.Review;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private final DataSourceProvider connectionProvider;

    public ReviewDAOImpl(DataSourceProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Review insert(Review review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) review.setId(keys.getLong(1));
            }
            return review;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert review", e);
        }
    }

    @Override
    public List<Review> findByProduct(long productId) {
        String sql = "SELECT r.*, u.name AS user_name FROM reviews r " +
                "JOIN users u ON u.id = r.user_id WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> reviews = new ArrayList<>();
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setUserName(rs.getString("user_name"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    r.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
                    reviews.add(r);
                }
                return reviews;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list reviews", e);
        }
    }

    @Override
    public boolean hasReviewedFromCompletedOrder(long userId, long productId) {
        String sql = "SELECT 1 FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existing review", e);
        }
    }

    /** F8: reviews are only allowed on products from the buyer's completed (DELIVERED) orders. */
    @Override
    public boolean buyerCompletedOrderForProduct(long userId, long productId) {
        String sql = "SELECT 1 FROM orders o " +
                "JOIN order_items oi ON oi.order_id = o.id " +
                "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED'";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to verify order completion", e);
        }
    }
}
