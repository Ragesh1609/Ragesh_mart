package com.ragesh.rageshmart.dao.impl;

import com.ragesh.rageshmart.dao.CartDAO;
import com.ragesh.rageshmart.exception.DataAccessException;
import com.ragesh.rageshmart.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private final DataSourceProvider connectionProvider;

    public CartDAOImpl(DataSourceProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void addOrIncrement(long userId, long productId, int quantity) {
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        try (Connection conn = connectionProvider.getConnection()) {
            if (existing.isPresent()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?")) {
                    ps.setInt(1, quantity);
                    ps.setLong(2, existing.get().getId());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)")) {
                    ps.setLong(1, userId);
                    ps.setLong(2, productId);
                    ps.setInt(3, quantity);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add/increment cart item", e);
        }
    }

    @Override
    public boolean updateQuantity(long userId, long cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update cart quantity", e);
        }
    }

    @Override
    public boolean remove(long userId, long cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove cart item", e);
        }
    }

    @Override
    public List<CartItem> findByUser(long userId) {
        String sql = "SELECT ci.id, ci.user_id, ci.product_id, ci.quantity, p.name, p.price " +
                "FROM cart_items ci JOIN products p ON p.id = ci.product_id " +
                "WHERE ci.user_id = ? ORDER BY ci.created_at";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartItem> items = new ArrayList<>();
                while (rs.next()) items.add(mapRow(rs));
                return items;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list cart items", e);
        }
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(long userId, long productId) {
        String sql = "SELECT ci.id, ci.user_id, ci.product_id, ci.quantity, p.name, p.price " +
                "FROM cart_items ci JOIN products p ON p.id = ci.product_id " +
                "WHERE ci.user_id = ? AND ci.product_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find cart item", e);
        }
    }

    @Override
    public void clear(long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear cart", e);
        }
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setProductName(rs.getString("name"));
        item.setUnitPrice(rs.getBigDecimal("price"));
        return item;
    }
}
