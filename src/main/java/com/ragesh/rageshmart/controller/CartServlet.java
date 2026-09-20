package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.CartItem;
import com.ragesh.rageshmart.service.CartService;
import com.ragesh.rageshmart.service.impl.CartServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * F4: cart add/update/remove, running total.
 * GET  /api/v1/cart              -> view cart + running total
 * POST /api/v1/cart              -> body {productId, quantity} add/increment
 * PUT  /api/v1/cart/{cartItemId} -> body {quantity} update
 * DELETE /api/v1/cart/{cartItemId} -> remove
 * AuthFilter guarantees req has an authenticated user by the time we get here.
 */
@WebServlet("/api/v1/cart/*")
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartServiceImpl(DAOFactory.cartDAO(), DAOFactory.productDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        List<CartItem> items = cartService.viewCart(userId);
        BigDecimal total = items.stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.ok(Map.of("items", items, "total", total)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        try {
            AddToCartRequest body = JsonUtil.readJson(req, AddToCartRequest.class);
            cartService.addItem(userId, body.productId, body.quantity);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(cartService.viewCart(userId)));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        try {
            long cartItemId = Long.parseLong(req.getPathInfo().substring(1));
            UpdateCartRequest body = JsonUtil.readJson(req, UpdateCartRequest.class);
            cartService.updateItem(userId, cartItemId, body.quantity);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(cartService.viewCart(userId)));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.fail("NOT_FOUND", e.getMessage()));
        } catch (NumberFormatException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Cart item id required in path"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        try {
            long cartItemId = Long.parseLong(req.getPathInfo().substring(1));
            cartService.removeItem(userId, cartItemId);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(cartService.viewCart(userId)));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.fail("NOT_FOUND", e.getMessage()));
        } catch (NumberFormatException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Cart item id required in path"));
        }
    }

    private static class AddToCartRequest {
        long productId;
        int quantity;
    }

    private static class UpdateCartRequest {
        int quantity;
    }
}
