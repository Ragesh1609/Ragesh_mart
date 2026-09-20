package com.ragesh.rageshmart.dao.impl;

import com.ragesh.rageshmart.dao.OrderDAO;
import com.ragesh.rageshmart.exception.DataAccessException;
import com.ragesh.rageshmart.model.Order;
import com.ragesh.rageshmart.model.OrderItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    private final DataSourceProvider connectionProvider;

    public OrderDAOImpl(DataSourceProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    /** Inserts the order and its line items as a single transaction. */
    @Override
    public Order insert(Order order) {
        try (Connection conn = connectionProvider.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO orders (buyer_id, status, total_amount) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, order.getBuyerId());
                    ps.setString(2, order.getStatus().name());
                    ps.setBigDecimal(3, order.getTotalAmount());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        order.setId(keys.getLong(1));
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
                    for (OrderItem item : order.getItems()) {
                        ps.setLong(1, order.getId());
                        ps.setLong(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setBigDecimal(4, item.getUnitPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Failed to place order, rolled back", e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to place order", e);
        }
    }

    @Override
    public Optional<Order> findById(long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Order order = mapOrderRow(rs);
                order.setItems(findItems(conn, id));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find order", e);
        }
    }

    @Override
    public List<Order> findByBuyer(long buyerId) {
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        return queryOrders(sql, buyerId);
    }

    @Override
    public List<Order> findBySeller(long sellerId) {
        // Orders containing at least one item whose product belongs to this seller
        String sql = "SELECT DISTINCT o.* FROM orders o " +
                "JOIN order_items oi ON oi.order_id = o.id " +
                "JOIN products p ON p.id = oi.product_id " +
                "WHERE p.seller_id = ? ORDER BY o.created_at DESC";
        return queryOrders(sql, sellerId);
    }

    @Override
    public List<Order> findAll() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders ORDER BY created_at DESC");
             ResultSet rs = ps.executeQuery()) {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setItems(findItems(conn, order.getId()));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list all orders", e);
        }
    }

    @Override
    public boolean updateStatus(long orderId, Order.Status status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update order status", e);
        }
    }

    private List<Order> queryOrders(String sql, long param) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItems(conn, order.getId()));
                    orders.add(order);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query orders", e);
        }
    }

    private List<OrderItem> findItems(Connection conn, long orderId) throws SQLException {
        String sql = "SELECT oi.product_id, oi.quantity, oi.unit_price, p.name " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(orderId);
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setProductName(rs.getString("name"));
                    items.add(item);
                }
                return items;
            }
        }
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setStatus(Order.Status.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        Timestamp ts = rs.getTimestamp("created_at");
        order.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return order;
    }
}
