package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Order insert(Order order);
    Optional<Order> findById(long id);
    List<Order> findByBuyer(long buyerId);
    List<Order> findBySeller(long sellerId);
    List<Order> findAll();
    boolean updateStatus(long orderId, Order.Status status);
}
