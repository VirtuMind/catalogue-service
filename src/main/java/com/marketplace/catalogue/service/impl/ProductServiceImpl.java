package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.model.Category;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.model.ProductStatus;
import com.marketplace.catalogue.repository.CategoryRepository;
import com.marketplace.catalogue.repository.ProductRepository;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.repository.ProductRepository;
import com.marketplace.catalogue.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import java.util.UUID;

@Service
@Transactional
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


    /*
     * Create a new product.
     */
    @Override
    public Product createProduct(ProductInput input) {
        // Create product entity
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setCategoryId(input.getCategoryId());
        product.setStatus(ProductStatus.valueOf(input.getStatus()));
        product.setBasePrice(input.getBasePrice());
        product.setInventory(input.getInventory());

        /*
         * Mn hna khsk t3yt ela service dyal media bach ysavi thumbnail o yrj3 lk id bach tsavih f db
         * o khsk t3yt ela service dyal stock bach ytracki product inventory
         * khsk t3yt ela service dyal promotion bach ytracki promotions
         * o khsk t3yt ela service dyal reviews bach ytracki reviews
         */


        // Save product in DB
        return productRepository.save(product);
    }
}
