package com.marketplace.catalogue.repository;

import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByStatus(Product.ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND p.status = :status")
    List<Product> findByCategoryIdAndStatus(@Param("categoryId") UUID categoryId, @Param("status") Product.ProductStatus status);


    void deleteById(UUID id);
}
