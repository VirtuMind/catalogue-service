package com.marketplace.catalogue.service;

import com.marketplace.catalogue.dto.ProductDetails;

import java.util.UUID;

import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.model.Product;

public interface ProductService {
    ProductDetails getProductDetails(UUID productId);

    Product createProduct(ProductInput input);

}
