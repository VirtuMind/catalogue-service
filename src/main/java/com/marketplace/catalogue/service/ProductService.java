package com.marketplace.catalogue.service;

import com.marketplace.catalogue.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    Product createProduct(Product product);

    Optional<Product> getProductById(UUID productId);

    List<Product> getProducts(UUID categoryId, Product.ProductStatus status);

    Product updateProduct(UUID productId, Product updatedProduct);

    void deleteProduct(UUID productId);
}
