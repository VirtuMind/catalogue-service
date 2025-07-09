package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.client.*;
import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.dto.ProductMeta;
import com.marketplace.catalogue.dto.external.ScenaUploadResponse;
import com.marketplace.catalogue.model.Category;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.model.ProductStatus;
import com.marketplace.catalogue.repository.CategoryRepository;
import com.marketplace.catalogue.repository.ProductRepository;
import com.marketplace.catalogue.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ScenaServiceClient scenaServiceClient;
    private final MetronomeServiceClient metronomeServiceClient;
    private final OrnamentoServiceClient ornamentoServiceClient;
    private final EchoServiceClient echoServiceClient;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ScenaServiceClient scenaServiceClient,
                              MetronomeServiceClient metronomeServiceClient,
                              OrnamentoServiceClient ornamentoServiceClient,
                              EchoServiceClient echoServiceClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.scenaServiceClient = scenaServiceClient;
        this.metronomeServiceClient = metronomeServiceClient;
        this.ornamentoServiceClient = ornamentoServiceClient;
        this.echoServiceClient = echoServiceClient;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetails getProductDetails(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return null;
        }

        ProductDetails productDetails = new ProductDetails();
        
        // Set basic product information
        productDetails.setId(product.getId());
        productDetails.setName(product.getName());
        productDetails.setDescription(product.getDescription());
        productDetails.setBasePrice(product.getBasePrice());
        productDetails.setStatus(product.getStatus());
        
        // Get category name
        String categoryName = categoryRepository.findByIdAndNotDeleted(product.getCategoryId())
                .map(Category::getName)
                .orElse(null);
        productDetails.setCategory(categoryName);
        
        // Get thumbnail URL from SCENA service
        String thumbnailUrl = scenaServiceClient.getThumbnailUrl(productId);
        productDetails.setThumbnailUrl(thumbnailUrl);
        
        // Get media URLs from SCENA service
        List<String> mediaUrls = scenaServiceClient.getProductMediaUrls(productId);
        productDetails.setMediaUrls(mediaUrls);
        
        // Get inventory from METRONOME service
        Integer inventory = metronomeServiceClient.getProductInventory(productId);
        productDetails.setInventory(inventory);
        
        // Get discount from ORNAMENTO service
        productDetails.setDiscount(ornamentoServiceClient.getProductDiscount(productId));
        
        // Get reviews from ECHO service
        productDetails.setReviews(echoServiceClient.getProductReviews(productId));
        
        return productDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductMeta getProductMeta(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return null;
        }

        ProductMeta productMeta = new ProductMeta();
        productMeta.setId(product.getId());
        productMeta.setName(product.getName());
        productMeta.setDescription(product.getDescription());
        productMeta.setBasePrice(product.getBasePrice());
        productMeta.setStatus(product.getStatus());
        
        // Get category name
        String categoryName = categoryRepository.findByIdAndNotDeleted(product.getCategoryId())
                .map(Category::getName)
                .orElse(null);
        productMeta.setCategory(categoryName);
        
        // Get thumbnail URL from SCENA service
        String thumbnailUrl = scenaServiceClient.getThumbnailUrl(productId);
        productMeta.setThumbnailUrl(thumbnailUrl);
        
        return productMeta;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDetails> getAllProductDetails(UUID categoryId, ProductStatus status) {
        List<Product> products;
        
        if (categoryId != null && status != null) {
            products = productRepository.findByCategoryIdAndStatus(categoryId, status);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (status != null) {
            products = productRepository.findByStatus(status);
        } else {
            products = productRepository.findAll();
        }
        
        return products.stream()
                .map(product -> getProductDetails(product.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductMeta> getAllProductMeta(UUID categoryId, ProductStatus status) {
        List<Product> products;
        
        if (categoryId != null && status != null) {
            products = productRepository.findByCategoryIdAndStatus(categoryId, status);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (status != null) {
            products = productRepository.findByStatus(status);
        } else {
            products = productRepository.findAll();
        }
        
        return products.stream()
                .map(product -> getProductMeta(product.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDetails createProduct(ProductInput input) {
        // Create product entity first to get the ID
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setCategoryId(input.getCategoryId());
        product.setStatus(ProductStatus.valueOf(input.getStatus().toUpperCase()));
        product.setBasePrice(input.getBasePrice());

        // Save product in DB first
        Product savedProduct = productRepository.save(product);
        
        // Upload thumbnail to SCENA service
        if (input.getThumbnailFile() != null && !input.getThumbnailFile().isEmpty()) {
            ScenaUploadResponse thumbnailResponse = scenaServiceClient.uploadThumbnail(
                    input.getThumbnailFile(), savedProduct.getId());
            if (thumbnailResponse == null) {
                throw new RuntimeException("Failed to upload thumbnail to media service");
            }
        }
        
        // Upload additional media files to SCENA service (loop through each file)
        if (input.getMediaFiles() != null && !input.getMediaFiles().isEmpty()) {
            for (MultipartFile mediaFile : input.getMediaFiles()) {
                ScenaUploadResponse mediaResponse = scenaServiceClient.uploadMediaFile(
                        mediaFile, savedProduct.getId());
                if (mediaResponse == null) {
                    // Log warning but don't fail the entire operation
                    System.err.println("Warning: Failed to upload media file " + mediaFile.getOriginalFilename());
                }
            }
        }
        
        // Return full product details
        return getProductDetails(savedProduct.getId());
    }

    @Override
    @Transactional
    public ProductDetails updateProduct(UUID productId, ProductInput input) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            return null;
        }
        
        // Update thumbnail if provided
        if (input.getThumbnailFile() != null && !input.getThumbnailFile().isEmpty()) {
            ScenaUploadResponse thumbnailResponse = scenaServiceClient.uploadThumbnail(
                    input.getThumbnailFile(), productId);
            if (thumbnailResponse == null) {
                throw new RuntimeException("Failed to upload thumbnail to media service");
            }
        }
        
        // Upload additional media files if provided (loop through each file)
        if (input.getMediaFiles() != null && !input.getMediaFiles().isEmpty()) {
            for (MultipartFile mediaFile : input.getMediaFiles()) {
                ScenaUploadResponse mediaResponse = scenaServiceClient.uploadMediaFile(
                        mediaFile, productId);
                if (mediaResponse == null) {
                    // Log warning but don't fail the entire operation
                    System.err.println("Warning: Failed to upload media file " + mediaFile.getOriginalFilename());
                }
            }
        }
        
        // Update product fields
        existingProduct.setName(input.getName());
        existingProduct.setDescription(input.getDescription());
        existingProduct.setCategoryId(input.getCategoryId());
        existingProduct.setStatus(ProductStatus.valueOf(input.getStatus().toUpperCase()));
        existingProduct.setBasePrice(input.getBasePrice());
        
        // Save updated product
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Return full product details
        return getProductDetails(updatedProduct.getId());
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            return false;
        }
        
        productRepository.deleteById(productId);
        return true;
    }
}
