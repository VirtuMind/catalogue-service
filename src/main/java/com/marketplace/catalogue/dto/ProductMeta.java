package com.marketplace.catalogue.dto;

import com.marketplace.catalogue.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMeta {
    private UUID id;
    
    private String name;
    
    private String description;
    
    private Double basePrice;
    
    private String category;
    
    private String thumbnailUrl;
    
    private ProductStatus status; // enum: [disponible, supprimé, rupture]
}
