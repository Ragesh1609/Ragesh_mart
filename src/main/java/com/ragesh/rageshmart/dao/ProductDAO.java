package com.ragesh.rageshmart.dao;

import com.ragesh.rageshmart.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product insert(Product product);
    Optional<Product> findById(long id);
    List<Product> search(String keyword, String category);
    List<Product> findBySeller(long sellerId);
    boolean update(Product product);
    boolean delete(long id, long sellerId);
    boolean adminDelete(long id);
    boolean decrementStock(long productId, int quantity);
}
