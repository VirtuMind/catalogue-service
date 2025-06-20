package com.marketplace.catalogue.repository.impl;

import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public Optional<Product> findById(UUID id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class)
                .getResultList();
    }

    @Override
    public List<Product> findByCategoryId(UUID categoryId) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.categoryId = :categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<Product> findByStatus(Product.ProductStatus status) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.status = :status", Product.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public void deleteById(UUID id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }
}
