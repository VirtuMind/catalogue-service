package com.marketplace.catalogue.service;

import com.marketplace.catalogue.dto.ProductDetails;

import java.util.UUID;

public interface ProductService {
    ProductDetails getProductDetails(UUID productId);
}
