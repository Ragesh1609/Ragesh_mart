package com.ragesh.rageshmart.service;

import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.Order;

import java.util.List;

public interface OrderService {
    /** F5: place an order from the buyer's current cart via mock payment confirmation. */
    Order checkout(long buyerId, boolean mockPaymentConfirmed) throws ValidationException;

    List<Order> buyerHistory(long buyerId);          // F6 (buyer)
    List<Order> sellerIncomingOrders(long sellerId);  // F6 (seller)
    List<Order> allOrders();                          // F7 (admin)

    Order updateStatus(long orderId, Order.Status status) throws NotFoundException; // O2
}
