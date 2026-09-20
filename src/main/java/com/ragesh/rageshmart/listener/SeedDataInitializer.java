package com.ragesh.rageshmart.listener;

import com.ragesh.rageshmart.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Seeds a demo ADMIN/SELLER/BUYER account (F1: admin has no signup flow, seed only)
 * and a few demo products, using real bcrypt hashes generated at runtime via jBCrypt
 * rather than a hardcoded hash string in seed.sql.
 *
 * CHANGE THESE DEMO PASSWORDS before any real deployment.
 */
final class SeedDataInitializer {

    private SeedDataInitializer() { }

    static void seedIfEmpty(Connection conn) throws SQLException {
        if (countUsers(conn) > 0) {
            return; // already seeded
        }

        long adminId = insertUser(conn, "Admin", "admin@rageshmart.com", "AdminPass123", "ADMIN");
        long sellerId = insertUser(conn, "Demo Seller", "seller@rageshmart.com", "SellerPass123", "SELLER");
        insertUser(conn, "Demo Buyer", "buyer@rageshmart.com", "BuyerPass123", "BUYER");

        insertProduct(conn, sellerId, "Wireless Mouse", "Ergonomic wireless mouse, 2.4GHz",
                new BigDecimal("599.00"), 50, "Electronics");
        insertProduct(conn, sellerId, "Mechanical Keyboard", "RGB backlit mechanical keyboard",
                new BigDecimal("2499.00"), 30, "Electronics");
        insertProduct(conn, sellerId, "Cotton T-Shirt", "Plain round-neck cotton t-shirt",
                new BigDecimal("399.00"), 100, "Apparel");
    }

    private static long countUsers(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static long insertUser(Connection conn, String name, String email, String plainPassword, String role)
            throws SQLException {
        String hash = PasswordUtil.hash(plainPassword);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private static void insertProduct(Connection conn, long sellerId, String name, String description,
                                       BigDecimal price, int stock, String category) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products (seller_id, name, description, price, stock_qty, category) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setLong(1, sellerId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setBigDecimal(4, price);
            ps.setInt(5, stock);
            ps.setString(6, category);
            ps.executeUpdate();
        }
    }
}
