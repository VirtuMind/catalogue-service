package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProductDetails(@PathVariable UUID productId) {
        ProductDetails productDetails = productService.getProductDetails(productId);
        if (productDetails != null) {
            return ResponseEntity.ok(productDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + productId);
        }
    }

    @PostMapping(path = "/" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createProduct(@ModelAttribute @Valid ProductInput input) {

        Product result = productService.createProduct(input);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
