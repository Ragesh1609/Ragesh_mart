package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.model.Review;

import java.util.List;

public interface ReviewDAO {
    Review insert(Review review);
    List<Review> findByProduct(long productId);
    boolean hasReviewedFromCompletedOrder(long userId, long productId);
    boolean buyerCompletedOrderForProduct(long userId, long productId);
}
