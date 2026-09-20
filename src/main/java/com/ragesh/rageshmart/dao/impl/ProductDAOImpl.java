package com.ragesh.rageshmart.dao.impl;

import com.ragesh.rageshmart.dao.ProductDAO;
import com.ragesh.rageshmart.exception.DataAccessException;
import com.ragesh.rageshmart.model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    private final DataSourceProvider connectionProvider;

    public ProductDAOImpl(DataSourceProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Product insert(Product p) {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getSellerId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setString(6, p.getCategory());
            ps.setString(7, p.getImageUrl());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }
            return p;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert product", e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find product", e);
        }
    }

    @Override
    public List<Product> search(String keyword, String category) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?)");
            String like = "%" + keyword.toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> results = new ArrayList<>();
                while (rs.next()) results.add(mapRow(rs));
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search products", e);
        }
    }

    @Override
    public List<Product> findBySeller(long sellerId) {
        String sql = "SELECT * FROM products WHERE seller_id = ? ORDER BY created_at DESC";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> results = new ArrayList<>();
                while (rs.next()) results.add(mapRow(rs));
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list seller products", e);
        }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock_qty=?, category=?, image_url=? " +
                "WHERE id=? AND seller_id=?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setString(5, p.getCategory());
            ps.setString(6, p.getImageUrl());
            ps.setLong(7, p.getId());
            ps.setLong(8, p.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update product", e);
        }
    }

    @Override
    public boolean delete(long id, long sellerId) {
        String sql = "DELETE FROM products WHERE id = ? AND seller_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, sellerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete product", e);
        }
    }

    @Override
    public boolean adminDelete(long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to admin-delete product", e);
        }
    }

    @Override
    public boolean decrementStock(long productId, int quantity) {
        // Guard clause in SQL prevents stock going negative under concurrent checkouts
        String sql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to decrement stock", e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        Timestamp ts = rs.getTimestamp("created_at");
        p.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return p;
    }
}
