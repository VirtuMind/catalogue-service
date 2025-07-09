package com.marketplace.catalogue.repository;

import com.marketplace.catalogue.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    Optional<Category> findById(UUID id);
    
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false")
    List<Category> findAllActive();
    
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Category> findByIdAndNotDeleted(@Param("id") UUID id);
    
    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.isDeleted = false")
    Optional<Category> findByNameAndNotDeleted(@Param("name") String name);
    
    @Modifying
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    int softDeleteById(@Param("id") UUID id);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.categoryId = :categoryId")
    long countProductsByCategoryId(@Param("categoryId") UUID categoryId);
}
