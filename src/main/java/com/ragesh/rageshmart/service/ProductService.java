package com.ragesh.rageshmart.service;

import com.ragesh.rageshmart.dto.ProductRequestDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.NotFoundException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.Product;

import java.util.List;

public interface ProductService {
    Product create(long sellerId, ProductRequestDTO request) throws ValidationException;
    Product update(long productId, long sellerId, ProductRequestDTO request) throws ValidationException, NotFoundException, AuthException;
    void delete(long productId, long sellerId) throws NotFoundException, AuthException;
    List<Product> search(String keyword, String category);
    List<Product> findBySeller(long sellerId);
    Product findById(long id) throws NotFoundException;
}
