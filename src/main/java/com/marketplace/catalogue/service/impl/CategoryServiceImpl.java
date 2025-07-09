package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.dto.CategoryInput;
import com.marketplace.catalogue.dto.CategoryResponse;
import com.marketplace.catalogue.model.Category;
import com.marketplace.catalogue.repository.CategoryRepository;
import com.marketplace.catalogue.service.CategoryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllActive()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findByIdAndNotDeleted(categoryId)
                .orElse(null);
        
        return category != null ? mapToResponse(category) : null;
    }
    
    @Override
    public CategoryResponse createCategory(CategoryInput input) {
        // Check if category with same name already exists
        if (categoryRepository.findByNameAndNotDeleted(input.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + input.getName() + "' already exists");
        }
        
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(input.getName());
        category.setIsDeleted(false);
        
        try {
            Category savedCategory = categoryRepository.save(category);
            return mapToResponse(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Category with name '" + input.getName() + "' already exists");
        }
    }
    
    @Override
    public CategoryResponse updateCategory(UUID categoryId, CategoryInput input) {
        Category existingCategory = categoryRepository.findByIdAndNotDeleted(categoryId)
                .orElse(null);
        
        if (existingCategory == null) {
            return null;
        }
        
        // Check if another category with same name exists (excluding current category)
        categoryRepository.findByNameAndNotDeleted(input.getName())
                .ifPresent(category -> {
                    if (!category.getId().equals(categoryId)) {
                        throw new IllegalArgumentException("Category with name '" + input.getName() + "' already exists");
                    }
                });
        
        existingCategory.setName(input.getName());
        
        try {
            Category updatedCategory = categoryRepository.save(existingCategory);
            return mapToResponse(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Category with name '" + input.getName() + "' already exists");
        }
    }
    
    @Override
    public boolean deleteCategory(UUID categoryId) {
        // Check if category exists and is not already deleted
        Category category = categoryRepository.findByIdAndNotDeleted(categoryId)
                .orElse(null);
        
        if (category == null) {
            return false;
        }
        
        // Check if category has associated products
        long productCount = categoryRepository.countProductsByCategoryId(categoryId);
        if (productCount > 0) {
            throw new IllegalArgumentException("Cannot delete category that has associated products. " +
                    "Please move or delete all products in this category first.");
        }
        
        // Perform soft delete
        int deletedRows = categoryRepository.softDeleteById(categoryId);
        return deletedRows > 0;
    }
    
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setIsDeleted(category.getIsDeleted());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
