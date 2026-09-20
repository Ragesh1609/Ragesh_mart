package com.ragesh.rageshmart.service.impl;

import com.ragesh.rageshmart.dao.CartDAO;
import com.ragesh.rageshmart.dao.OrderDAO;
import com.ragesh.rageshmart.dao.ProductDAO;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.CartItem;
import com.ragesh.rageshmart.model.Order;
import com.ragesh.rageshmart.model.OrderItem;
import com.ragesh.rageshmart.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

/**
 * F5: checkout via a mock payment confirmation step (Scope constraint: no real
 * payment gateway - {@code mockPaymentConfirmed} simulates the user clicking
 * "Confirm Payment" on a mock screen). F6/F7 read paths. O2 status workflow.
 */
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderServiceImpl(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public Order checkout(long buyerId, boolean mockPaymentConfirmed) throws ValidationException {
        if (!mockPaymentConfirmed) {
            throw new ValidationException("payment", "Mock payment must be confirmed to place the order");
        }

        List<CartItem> cartItems = cartDAO.findByUser(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("cart", "Cart is empty");
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(Order.Status.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            boolean reserved = productDAO.decrementStock(item.getProductId(), item.getQuantity());
            if (!reserved) {
                throw new ValidationException("stock",
                        "Insufficient stock for " + item.getProductName());
            }
            order.getItems().add(new OrderItem(item.getProductId(), item.getQuantity(), item.getUnitPrice()));
            total = total.add(item.getLineTotal());
        }
        order.setTotalAmount(total);

        Order saved = orderDAO.insert(order);
        cartDAO.clear(buyerId);
        return saved;
    }

    @Override
    public List<Order> buyerHistory(long buyerId) {
        return orderDAO.findByBuyer(buyerId);
    }

    @Override
    public List<Order> sellerIncomingOrders(long sellerId) {
        return orderDAO.findBySeller(sellerId);
    }

    @Override
    public List<Order> allOrders() {
        return orderDAO.findAll();
    }

    @Override
    public Order updateStatus(long orderId, Order.Status status) throws NotFoundException {
        boolean updated = orderDAO.updateStatus(orderId, status);
        if (!updated) {
            throw new NotFoundException("Order not found");
        }
        return orderDAO.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
    }
}
