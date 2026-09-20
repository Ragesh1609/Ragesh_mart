package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartDAO {
    void addOrIncrement(long userId, long productId, int quantity);
    boolean updateQuantity(long userId, long cartItemId, int quantity);
    boolean remove(long userId, long cartItemId);
    List<CartItem> findByUser(long userId);
    Optional<CartItem> findByUserAndProduct(long userId, long productId);
    void clear(long userId);
}
