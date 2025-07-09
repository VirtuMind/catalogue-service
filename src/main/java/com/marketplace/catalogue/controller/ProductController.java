package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ApiResponse;
import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.dto.ProductMeta;
import com.marketplace.catalogue.model.ProductStatus;
import com.marketplace.catalogue.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product with complete details
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDetails>> createProduct(@ModelAttribute @Valid ProductInput input) {
        try {
            ProductDetails result = productService.createProduct(input);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created("Produit créé avec succès", result));
        } catch (Exception e) {
            throw new RuntimeException("Échec de la création du produit: " + e.getMessage());
        }
    }

    /**
     * Get list of products with complete details
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDetails>>> listProductsFull(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String status) {
        
        UUID categoryUuid = null;
        ProductStatus productStatus = null;
        
        try {
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                categoryUuid = UUID.fromString(categoryId);
            }
            if (status != null && !status.trim().isEmpty()) {
                productStatus = ProductStatus.valueOf(status.toLowerCase());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Paramètres invalides: " + e.getMessage()));
        }
        
        List<ProductDetails> products = productService.getAllProductDetails(categoryUuid, productStatus);
        return ResponseEntity.ok(ApiResponse.ok("Liste des produits récupérée avec succès", products));
    }

    /**
     * Get complete details of a specific product
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetails>> getProductDetailsById(@PathVariable UUID productId) {
        ProductDetails productDetails = productService.getProductDetails(productId);
        if (productDetails != null) {
            return ResponseEntity.ok(ApiResponse.ok("Détails du produit récupérés avec succès", productDetails));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Produit non trouvé avec l'ID: " + productId));
        }
    }

    /**
     * Update a product (all fields except reviews)
     */
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDetails>> updateProduct(
            @PathVariable UUID productId,
            @ModelAttribute @Valid ProductInput input) {
        try {
            ProductDetails result = productService.updateProduct(productId, input);
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.ok("Produit mis à jour avec succès", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Produit non trouvé avec l'ID: " + productId));
            }
        } catch (Exception e) {
            throw new RuntimeException("Échec de la mise à jour du produit: " + e.getMessage());
        }
    }

    /**
     * Delete a product
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId) {
        boolean deleted = productService.deleteProduct(productId);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.noContent("Produit supprimé avec succès"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Produit non trouvé avec l'ID: " + productId));
        }
    }

    /**
     * Get list of products (metadata only)
     */
    @GetMapping("/meta")
    public ResponseEntity<ApiResponse<List<ProductMeta>>> listProductsMeta(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String status) {
        
        UUID categoryUuid = null;
        ProductStatus productStatus = null;
        
        try {
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                categoryUuid = UUID.fromString(categoryId);
            }
            if (status != null && !status.trim().isEmpty()) {
                productStatus = ProductStatus.valueOf(status.toLowerCase());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Paramètres invalides: " + e.getMessage()));
        }
        
        List<ProductMeta> products = productService.getAllProductMeta(categoryUuid, productStatus);
        return ResponseEntity.ok(ApiResponse.ok("Métadonnées des produits récupérées avec succès", products));
    }

    /**
     * Get metadata of a specific product
     */
    @GetMapping("/meta/{productId}")
    public ResponseEntity<ApiResponse<ProductMeta>> getProductMetaById(@PathVariable UUID productId) {
        ProductMeta productMeta = productService.getProductMeta(productId);
        if (productMeta != null) {
            return ResponseEntity.ok(ApiResponse.ok("Métadonnées du produit récupérées avec succès", productMeta));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Produit non trouvé avec l'ID: " + productId));
        }
    }
}
