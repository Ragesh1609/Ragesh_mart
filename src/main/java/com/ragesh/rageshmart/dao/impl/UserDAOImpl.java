package com.ragesh.rageshmart.dao.impl;

import com.ragesh.rageshmart.dao.UserDAO;
import com.ragesh.rageshmart.exception.DataAccessException;
import com.ragesh.rageshmart.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All SQL for the users table lives here. PreparedStatement only (Section 2, rule 1);
 * try-with-resources on every Connection/PreparedStatement/ResultSet (rule 6).
 */
public class UserDAOImpl implements UserDAO {

    private final DataSourceProvider connectionProvider;

    public UserDAOImpl(DataSourceProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public User insert(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert user", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by id", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find user by email", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list users", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check user existence", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        Timestamp ts = rs.getTimestamp("created_at");
        user.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return user;
    }
}
