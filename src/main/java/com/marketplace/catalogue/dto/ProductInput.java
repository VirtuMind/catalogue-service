package com.marketplace.catalogue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInput {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Status is required")
    private String status; // enum: [disponible, supprimé, rupture]

    @NotNull(message = "Thumbnail file is required")
    private MultipartFile thumbnailFile;

    private List<MultipartFile> mediaFiles;

    private Discount discount;

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        @NotNull
        private Float discountPercentage;
        @NotNull
        private Float discountPrice;
        @NotNull
        private LocalDate startDate;
        @NotNull
        private LocalDate endDate;
    }
}
