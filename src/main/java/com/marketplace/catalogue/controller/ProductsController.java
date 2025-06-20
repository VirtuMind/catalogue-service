package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetails> getProductDetails(@PathVariable UUID productId) {
        ProductDetails productDetails = productService.getProductDetails(productId);
        if (productDetails != null) {
            return ResponseEntity.ok(productDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
