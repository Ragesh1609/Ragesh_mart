package com.ragesh.rageshmart.service.impl;

import com.ragesh.rageshmart.dao.CartDAO;
import com.ragesh.rageshmart.dao.ProductDAO;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.CartItem;
import com.ragesh.rageshmart.model.Product;
import com.ragesh.rageshmart.service.CartService;
import com.ragesh.rageshmart.util.ValidationUtil;

import java.util.List;

/** F4: add/update/remove cart items, running total is computed by CartItem.getLineTotal(). */
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public void addItem(long userId, long productId, int quantity) throws ValidationException, NotFoundException {
        if (quantity <= 0) {
            throw new ValidationException("quantity", "Quantity must be at least 1");
        }
        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getStockQty() < quantity) {
            throw new ValidationException("quantity", "Not enough stock available");
        }
        cartDAO.addOrIncrement(userId, productId, quantity);
    }

    @Override
    public void updateItem(long userId, long cartItemId, int quantity) throws ValidationException, NotFoundException {
        if (!ValidationUtil.isNonNegativeInt(quantity) || quantity == 0) {
            throw new ValidationException("quantity", "Quantity must be at least 1 (use remove to delete)");
        }
        boolean updated = cartDAO.updateQuantity(userId, cartItemId, quantity);
        if (!updated) {
            throw new NotFoundException("Cart item not found");
        }
    }

    @Override
    public void removeItem(long userId, long cartItemId) throws NotFoundException {
        boolean removed = cartDAO.remove(userId, cartItemId);
        if (!removed) {
            throw new NotFoundException("Cart item not found");
        }
    }

    @Override
    public List<CartItem> viewCart(long userId) {
        return cartDAO.findByUser(userId);
    }
}
