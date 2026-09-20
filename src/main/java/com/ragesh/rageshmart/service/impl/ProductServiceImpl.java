package com.ragesh.rageshmart.service.impl;

import com.ragesh.rageshmart.dao.ProductDAO;
import com.ragesh.rageshmart.dto.ProductRequestDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.Product;
import com.ragesh.rageshmart.service.ProductService;
import com.ragesh.rageshmart.util.ValidationUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** F2 (seller CRUD) and F3 (buyer browse/search) business logic. No JDBC here. */
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Product create(long sellerId, ProductRequestDTO request) throws ValidationException {
        validate(request);
        Product product = new Product();
        product.setSellerId(sellerId);
        applyRequest(product, request);
        return productDAO.insert(product);
    }

    @Override
    public Product update(long productId, long sellerId, ProductRequestDTO request)
            throws ValidationException, NotFoundException, AuthException {
        validate(request);
        Product existing = productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthException("You do not own this listing", HttpServletResponse.SC_FORBIDDEN);
        }
        applyRequest(existing, request);
        productDAO.update(existing);
        return existing;
    }

    @Override
    public void delete(long productId, long sellerId) throws NotFoundException, AuthException {
        Product existing = productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthException("You do not own this listing", HttpServletResponse.SC_FORBIDDEN);
        }
        productDAO.delete(productId, sellerId);
    }

    @Override
    public List<Product> search(String keyword, String category) {
        return productDAO.search(keyword, category);
    }

    @Override
    public List<Product> findBySeller(long sellerId) {
        return productDAO.findBySeller(sellerId);
    }

    @Override
    public Product findById(long id) throws NotFoundException {
        return productDAO.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private void validate(ProductRequestDTO r) throws ValidationException {
        if (ValidationUtil.isBlank(r.getName())) {
            throw new ValidationException("name", "Product name is required");
        }
        if (!ValidationUtil.isPositive(r.getPrice())) {
            throw new ValidationException("price", "Price must be greater than zero");
        }
        if (r.getStockQty() == null || !ValidationUtil.isNonNegativeInt(r.getStockQty())) {
            throw new ValidationException("stockQty", "Stock quantity cannot be negative");
        }
    }

    private void applyRequest(Product product, ProductRequestDTO r) {
        product.setName(r.getName().trim());
        product.setDescription(r.getDescription());
        product.setPrice(r.getPrice());
        product.setStockQty(r.getStockQty());
        product.setCategory(r.getCategory());
        product.setImageUrl(r.getImageUrl());
    }
}
