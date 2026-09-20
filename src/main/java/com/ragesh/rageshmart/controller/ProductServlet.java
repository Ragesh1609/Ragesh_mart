package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.dto.ProductRequestDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.Product;
import com.ragesh.rageshmart.model.User;
import com.ragesh.rageshmart.service.ProductService;
import com.ragesh.rageshmart.service.impl.ProductServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Maps to /api/v1/products and /api/v1/products/{id}.
 * GET is public (F3 browse/search); POST/PUT/DELETE require an authenticated SELLER
 * who owns the listing (F2). Thin servlet: no SQL, delegates to ProductService.
 */
@WebServlet("/api/v1/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl(DAOFactory.productDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                if ("true".equals(req.getParameter("mine"))) {
                    User seller = AuthFilter.currentUser(req);
                    if (seller == null || seller.getRole() != User.Role.SELLER) {
                        JsonUtil.writeJson(resp, HttpServletResponse.SC_FORBIDDEN,
                                ApiResponse.fail("FORBIDDEN", "Seller login required"));
                        return;
                    }
                    JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                            ApiResponse.ok(productService.findBySeller(seller.getId())));
                    return;
                }
                String keyword = req.getParameter("keyword");
                String category = req.getParameter("category");
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                        ApiResponse.ok(productService.search(keyword, category)));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                Product product = productService.findById(id);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(product));
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Invalid product id"));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User seller = requireSeller(req, resp);
        if (seller == null) return;
        try {
            ProductRequestDTO request = JsonUtil.readJson(req, ProductRequestDTO.class);
            Product product = productService.create(seller.getId(), request);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(product));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User seller = requireSeller(req, resp);
        if (seller == null) return;
        try {
            long id = Long.parseLong(req.getPathInfo().substring(1));
            ProductRequestDTO request = JsonUtil.readJson(req, ProductRequestDTO.class);
            Product product = productService.update(id, seller.getId(), request);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(product));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.fail("NOT_FOUND", e.getMessage()));
        } catch (AuthException e) {
            JsonUtil.writeJson(resp, e.getStatusCode(), ApiResponse.fail("FORBIDDEN", e.getMessage()));
        } catch (NumberFormatException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id required in path"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User seller = requireSeller(req, resp);
        if (seller == null) return;
        try {
            long id = Long.parseLong(req.getPathInfo().substring(1));
            productService.delete(id, seller.getId());
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(null));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.fail("NOT_FOUND", e.getMessage()));
        } catch (AuthException e) {
            JsonUtil.writeJson(resp, e.getStatusCode(), ApiResponse.fail("FORBIDDEN", e.getMessage()));
        } catch (NumberFormatException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id required in path"));
        }
    }

    /** Returns the logged-in seller, or writes a 401/403 response and returns null. */
    private User requireSeller(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = AuthFilter.currentUser(req);
        if (user == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return null;
        }
        if (user.getRole() != User.Role.SELLER) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_FORBIDDEN,
                    ApiResponse.fail("FORBIDDEN", "Seller role required"));
            return null;
        }
        return user;
    }
}
