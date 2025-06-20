package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.model.Category;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.repository.CategoryRepository;
import com.marketplace.catalogue.repository.ProductRepository;
import com.marketplace.catalogue.service.ProductService;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDetails getProductDetails(UUID productId) {
        ProductDetails productDetails = new ProductDetails();
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return null;
        } else {
            // Populate ProductDetails using data from our database
            productDetails.setId(product.getId());
            productDetails.setName(product.getName());
            productDetails.setDescription(product.getDescription());
            productDetails.setCategory(categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName)
                    .orElse(null));
            productDetails.setBasePrice(product.getBasePrice());
            // Call external service to get additional product details
            // get media files and thumbnail from scena
            // get stock from metronome
            // get promotions from ornamento
            // get reviews from echo
        }
        return productDetails;
    }
}
