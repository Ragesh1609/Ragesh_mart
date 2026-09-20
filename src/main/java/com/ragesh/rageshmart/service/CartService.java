package com.ragesh.rageshmart.service;

import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.CartItem;

import java.util.List;

public interface CartService {
    void addItem(long userId, long productId, int quantity) throws ValidationException, NotFoundException;
    void updateItem(long userId, long cartItemId, int quantity) throws ValidationException, NotFoundException;
    void removeItem(long userId, long cartItemId) throws NotFoundException;
    List<CartItem> viewCart(long userId);
}
