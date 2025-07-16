package com.marketplace.catalogue.service.impl;

import com.marketplace.catalogue.client.*;
import com.marketplace.catalogue.dto.Discount;
import com.marketplace.catalogue.dto.ProductDetails;
import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.dto.ProductMeta;
import com.marketplace.catalogue.dto.external.MetronomeInventoryRequest;
import com.marketplace.catalogue.dto.external.OrnamentoDiscountRequest;
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
        product.setStatus(ProductStatus.valueOf(input.getStatus().toLowerCase()));
        product.setBasePrice(input.getBasePrice());

        // Save product in DB first
        Product savedProduct = productRepository.save(product);
        
        // Upload thumbnail to SCENA service
        if (input.getThumbnailFile() != null && !input.getThumbnailFile().isEmpty()) {
            ScenaUploadResponse thumbnailResponse = scenaServiceClient.uploadMediaFile(
                    input.getThumbnailFile(), savedProduct.getId(), true);
            if (thumbnailResponse == null) {
                throw new RuntimeException("Failed to upload thumbnail to media service");
            }
        }

        // Upload additional media files to SCENA service (loop through each file)
        if (input.getMediaFiles() != null && !input.getMediaFiles().isEmpty()) {
            for (MultipartFile mediaFile : input.getMediaFiles()) {
                ScenaUploadResponse mediaResponse = scenaServiceClient.uploadMediaFile(
                        mediaFile, savedProduct.getId(), false);
                if (mediaResponse == null) {
                    // Log warning but don't fail the entire operation
                    System.err.println("Warning: Failed to upload media file " + mediaFile.getOriginalFilename());
                }
            }
        }

        // Send inventory to METRONOME service
        MetronomeInventoryRequest request = new MetronomeInventoryRequest(
                savedProduct.getId().toString(),
                input.getInventory()
        );
        boolean result =  metronomeServiceClient.increaseProductInventory(request);
        if (!result) {
            throw new RuntimeException("Failed to add product inventory in METRONOME service");
        }

        // Send discount to ORNAMENTO service if provided
        if (input.getDiscount() != null) {
            OrnamentoDiscountRequest discountRequest = new OrnamentoDiscountRequest(
                    savedProduct.getId().toString(),
                    input.getDiscount().getDiscountPercentage(),
                    input.getDiscount().getStartDate(),
                    input.getDiscount().getEndDate()
            );
            boolean discountResult = ornamentoServiceClient.addNewProductDiscount(discountRequest);
            if (!discountResult) {
                throw new RuntimeException("Failed to add product discount in ORNAMENTO service");
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
        
        // Update thumbnail if provided by deleting the old one and uploading the new one
        if (input.getThumbnailFile() != null && !input.getThumbnailFile().isEmpty()) {
            // Delete old thumbnail from SCENA service by getting the existing thumbnail ID
            String existingThumbnailId = scenaServiceClient.getThumbnailUrl(productId);
            if (existingThumbnailId != null) {
                boolean result = scenaServiceClient.deleteMedia(existingThumbnailId);
                if (!result) {
                    throw new RuntimeException("Failed to delete old thumbnail from media service");
                }
            }
            ScenaUploadResponse thumbnailResponse = scenaServiceClient.uploadMediaFile(
                    input.getThumbnailFile(), productId, true);
            if (thumbnailResponse == null) {
                throw new RuntimeException("Failed to upload new thumbnail to media service");
            }
        }
        
        // Update media files if provided by deleting old ones and uploading new ones
        if (input.getMediaFiles() != null && !input.getMediaFiles().isEmpty()) {
            //Get all existing media files for the product
            List<String> existingMediaIds = scenaServiceClient.getProductMediaIds(productId);

            // Delete old media files from SCENA service
            for (String mediaId : existingMediaIds) {
                boolean result = scenaServiceClient.deleteMedia(mediaId);
                if (!result) {
                    throw new RuntimeException("Failed to delete old media file from media service");
                }
            }

            // Upload new media file to SCENA service
            for (MultipartFile mediaFile : input.getMediaFiles()) {
                ScenaUploadResponse mediaResponse = scenaServiceClient.uploadMediaFile(
                        mediaFile, productId, false);
                if (mediaResponse == null) {
                    throw new RuntimeException("Failed to upload new media file to media service");
                }
            }
        }

        // Update inventory in METRONOME service by calculating the difference
        Integer currentInventory = metronomeServiceClient.getProductInventory(productId);
        if (currentInventory == null) {
            throw new RuntimeException("Failed to retrieve current inventory from METRONOME service");
        }
        int inventoryDifference = input.getInventory() - currentInventory;
        if (inventoryDifference > 0) {
            MetronomeInventoryRequest inventoryRequest = new MetronomeInventoryRequest(
                    productId.toString(),
                    inventoryDifference
            );
            boolean inventoryResult = metronomeServiceClient.increaseProductInventory(inventoryRequest);
            if (!inventoryResult) {
                throw new RuntimeException("Failed to increase product inventory in METRONOME service");
            }
        }
        else if (inventoryDifference < 0) {
            // If inventory is decreasing, we need to handle it accordingly
            MetronomeInventoryRequest inventoryRequest = new MetronomeInventoryRequest(
                    productId.toString(),
                    Math.abs(inventoryDifference)
            );
            boolean inventoryResult = metronomeServiceClient.decreaseProductInventory(inventoryRequest);
            if (!inventoryResult) {
                throw new RuntimeException("Failed to decrease product inventory in METRONOME service");
            }
        }
        // If inventory is unchanged, we do nothing

        // Update discount in ORNAMENTO service if provided by deleting the old one and adding the new one
        if (input.getDiscount() != null) {
            // Get existing discount from ORNAMENTO service
            Long existingDiscountId = ornamentoServiceClient.getProductDiscountId(productId);

            // Update discount
            OrnamentoDiscountRequest discountRequest = new OrnamentoDiscountRequest(
                    productId.toString(),
                    input.getDiscount().getDiscountPercentage(),
                    input.getDiscount().getStartDate(),
                    input.getDiscount().getEndDate()
            );
            boolean discountResult = ornamentoServiceClient.updateProductDiscount(productId.toString(), discountRequest);
            if (!discountResult) {
                throw new RuntimeException("Failed to add new product discount in ORNAMENTO service");
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
