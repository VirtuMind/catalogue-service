package com.marketplace.catalogue.repository;

import com.marketplace.catalogue.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findByCategoryId(UUID categoryId);
    List<Product> findByStatus(Product.ProductStatus status);
    void deleteById(UUID id);
}
