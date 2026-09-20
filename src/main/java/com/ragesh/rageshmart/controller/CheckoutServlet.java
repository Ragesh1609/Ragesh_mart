package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.Order;
import com.ragesh.rageshmart.service.OrderService;
import com.ragesh.rageshmart.service.impl.OrderServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * F5: place an order from the current cart via a mock payment confirmation step.
 * POST /api/v1/checkout   body: { "mockPaymentConfirmed": true }
 * Scope constraint: no real payment gateway is ever contacted here.
 */
@WebServlet("/api/v1/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrderService orderService =
            new OrderServiceImpl(DAOFactory.orderDAO(), DAOFactory.cartDAO(), DAOFactory.productDAO());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        try {
            CheckoutRequest body = JsonUtil.readJson(req, CheckoutRequest.class);
            boolean confirmed = body != null && body.mockPaymentConfirmed;
            Order order = orderService.checkout(userId, confirmed);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(order));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        }
    }

    private static class CheckoutRequest {
        boolean mockPaymentConfirmed;
    }
}
