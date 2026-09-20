package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.Review;
import com.ragesh.rageshmart.service.ReviewService;
import com.ragesh.rageshmart.service.impl.ReviewServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * F8: product reviews/ratings, restricted to delivered orders.
 * GET  /api/v1/reviews?productId=1   -> public, list reviews
 * POST /api/v1/reviews               -> body {productId, rating, comment}, buyer only
 */
@WebServlet("/api/v1/reviews")
public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewServiceImpl(DAOFactory.reviewDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long productId = Long.parseLong(req.getParameter("productId"));
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(reviewService.forProduct(productId)));
        } catch (NumberFormatException | NullPointerException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "productId query param is required"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = AuthFilter.currentUserId(req);
        try {
            ReviewRequest body = JsonUtil.readJson(req, ReviewRequest.class);
            Review review = reviewService.addReview(userId, body.productId, body.rating, body.comment);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(review));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        }
    }

    private static class ReviewRequest {
        long productId;
        int rating;
        String comment;
    }
}
