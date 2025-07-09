package com.marketplace.catalogue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInput {

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2-100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, max = 1000, message = "Description must be 10-1000 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Price must be ≥ 0.01")
    @DecimalMax(value = "999999.99", message = "Price must be ≤ 999,999.99")
    private Double basePrice;

    @NotNull(message = "Inventory count is required")
    @Min(value = 0, message = "Inventory cannot be negative")
    @Max(value = 99999, message = "Inventory cannot exceed 99,999")
    private Integer inventory;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "available|deleted |out_of_stock",
            message = "Status must be 'available', 'deleted', or 'out_of_stock'")
    private String status;

    @NotNull(message = "Thumbnail image is required")
    private MultipartFile thumbnailFile;

    @Size(max = 10, message = "Maximum 10 media files allowed")
    private List<MultipartFile> mediaFiles;

    @Valid
    private Discount discount;
}