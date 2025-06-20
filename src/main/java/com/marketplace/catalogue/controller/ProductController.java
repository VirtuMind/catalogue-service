package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ProductInput;
import com.marketplace.catalogue.model.Media;
import com.marketplace.catalogue.model.Product;
import com.marketplace.catalogue.repository.MediaRepository;
import com.marketplace.catalogue.service.FileStorageService;
import com.marketplace.catalogue.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final MediaRepository mediaRepository;

    @Autowired
    public ProductController(ProductService productService,
                             FileStorageService fileStorageService,
                             MediaRepository mediaRepository) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
        this.mediaRepository = mediaRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@ModelAttribute @Valid ProductInput input) {
        // Save the thumbnail
        String thumbnailUrl = fileStorageService.save(input.getThumbnailFile());

        // Create product entity
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setCategoryId(input.getCategoryId());
        product.setStatus(Product.ProductStatus.valueOf(input.getStatus()));
        product.setThumbnailUrl(thumbnailUrl);

        // Save product in DB
        Product savedProduct = productService.createProduct(product);

        // Save media files if any
        if (input.getMediaFiles() != null) {
            for (MultipartFile mediaFile : input.getMediaFiles()) {
                String mediaUrl = fileStorageService.save(mediaFile);
                Media media = new Media();
                media.setId(UUID.randomUUID());
                media.setProductId(savedProduct.getId());
                media.setUrl(mediaUrl);
                mediaRepository.save(media);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
}
