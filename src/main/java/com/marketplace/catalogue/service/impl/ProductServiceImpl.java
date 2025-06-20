package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.repository.ProductRepository;
import com.marketplace.catalogue.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a new product.
     */
    @Override
    public Product createProduct(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        // Future: Validate fields, call inventory service, etc.
        return productRepository.save(product);
    }

    /**
     * Get a product by ID.
     */
    @Override
    public Optional<Product> getProductById(UUID productId) {
        return productRepository.findById(productId);
    }

    /**
     * Get products based on optional category and status filters.
     */
    @Override
    public List<Product> getProducts(UUID categoryId, Product.ProductStatus status) {
        if (categoryId != null && status != null) {
            return productRepository.findByCategoryIdAndStatus(categoryId, status);
        } else if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        } else if (status != null) {
            return productRepository.findByStatus(status);
        } else {
            return productRepository.findAll();
        }
    }

    /**
     * Update an existing product.
     */
    @Override
    public Product updateProduct(UUID productId, Product updatedProduct) {
        return productRepository.findById(productId)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setDescription(updatedProduct.getDescription());
                    existing.setCategoryId(updatedProduct.getCategoryId());
                    existing.setStatus(updatedProduct.getStatus());
                    existing.setThumbnailUrl(updatedProduct.getThumbnailUrl());
                    // You could also handle updated media or price/discount here
                    return productRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    }

    /**
     * Delete a product by ID.
     */
    @Override
    public void deleteProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }
}
