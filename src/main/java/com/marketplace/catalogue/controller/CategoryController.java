package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ApiResponse;
import com.marketplace.catalogue.dto.CategoryInput;
import com.marketplace.catalogue.dto.CategoryResponse;
import com.marketplace.catalogue.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * Get all categories (public endpoint)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Liste des catégories récupérée avec succès", categories));
    }
    
    /**
     * Get category by ID (public endpoint)
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID categoryId) {
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        if (category != null) {
            return ResponseEntity.ok(ApiResponse.ok("Catégorie récupérée avec succès", category));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Catégorie non trouvée avec l'ID: " + categoryId));
        }
    }
    
    /**
     * Create a new category (requires authentication)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryInput input) {
        try {
            CategoryResponse result = categoryService.createCategory(input);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created("Catégorie créée avec succès", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.conflict(e.getMessage()));
        }
    }
    
    /**
     * Update an existing category (requires authentication)
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID categoryId,
            @RequestBody @Valid CategoryInput input) {
        try {
            CategoryResponse result = categoryService.updateCategory(categoryId, input);
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.ok("Catégorie mise à jour avec succès", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Catégorie non trouvée avec l'ID: " + categoryId));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.conflict(e.getMessage()));
        }
    }
    
    /**
     * Soft delete a category (requires authentication)
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID categoryId) {
        try {
            boolean deleted = categoryService.deleteCategory(categoryId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.noContent("Catégorie supprimée avec succès"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Catégorie non trouvée avec l'ID: " + categoryId));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
        }
    }
}
