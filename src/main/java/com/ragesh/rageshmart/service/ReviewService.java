package com.ragesh.rageshmart.service;

import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(long userId, long productId, int rating, String comment) throws ValidationException;
    List<Review> forProduct(long productId);
}
