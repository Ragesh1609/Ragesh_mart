package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.dto.UserResponseDTO;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.User;
import com.ragesh.rageshmart.service.OrderService;
import com.ragesh.rageshmart.service.impl.OrderServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * F7: admin view of all users and orders, moderate/remove listings.
 * GET    /api/v1/admin/users
 * GET    /api/v1/admin/orders
 * DELETE /api/v1/admin/products/{id}
 * All routes require ADMIN role (enforced here in addition to AuthFilter's session check).
 */
@WebServlet("/api/v1/admin/*")
public class AdminServlet extends HttpServlet {

    private final OrderService orderService =
            new OrderServiceImpl(DAOFactory.orderDAO(), DAOFactory.cartDAO(), DAOFactory.productDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!requireAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo(); // /users or /orders
        if ("/users".equals(pathInfo)) {
            List<UserResponseDTO> users = DAOFactory.userDAO().findAll().stream()
                    .map(UserResponseDTO::from).toList();
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(users));
        } else if ("/orders".equals(pathInfo)) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(orderService.allOrders()));
        } else {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", "Unknown admin route"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!requireAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo(); // /products/{id}
        try {
            String[] parts = pathInfo.substring(1).split("/");
            if (!"products".equals(parts[0])) {
                JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        ApiResponse.fail("NOT_FOUND", "Unknown admin route"));
                return;
            }
            long productId = Long.parseLong(parts[1]);
            boolean removed = DAOFactory.productDAO().adminDelete(productId);
            if (!removed) {
                JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        ApiResponse.fail("NOT_FOUND", "Product not found"));
                return;
            }
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(null));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Invalid admin product route"));
        }
    }

    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = AuthFilter.currentUser(req);
        if (user == null || user.getRole() != User.Role.ADMIN) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_FORBIDDEN,
                    ApiResponse.fail("FORBIDDEN", "Admin role required"));
            return false;
        }
        return true;
    }
}
