package com.marketplace.catalogue.service;

import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.dto.ProductMeta;
import com.marketplace.catalogue.model.ProductStatus;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDetails getProductDetails(UUID productId);
    
    ProductMeta getProductMeta(UUID productId);
    
    List<ProductDetails> getAllProductDetails(UUID categoryId, ProductStatus status);
    
    List<ProductMeta> getAllProductMeta(UUID categoryId, ProductStatus status);

    ProductDetails createProduct(ProductInput input);
    
    ProductDetails updateProduct(UUID productId, ProductInput input);
    
    boolean deleteProduct(UUID productId);
}
