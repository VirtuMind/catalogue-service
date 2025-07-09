package com.marketplace.catalogue.service;

import com.marketplace.catalogue.dto.CategoryInput;
import com.marketplace.catalogue.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    
    List<CategoryResponse> getAllCategories();
    
    CategoryResponse getCategoryById(UUID categoryId);
    
    CategoryResponse createCategory(CategoryInput input);
    
    CategoryResponse updateCategory(UUID categoryId, CategoryInput input);
    
    boolean deleteCategory(UUID categoryId);
}
