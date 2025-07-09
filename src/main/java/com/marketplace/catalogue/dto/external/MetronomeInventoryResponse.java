package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetronomeInventoryResponse {
    
    @JsonProperty("product_id")
    private String productId;
    
    @JsonProperty("available_quantity")
    private Integer availableQuantity;
    
    @JsonProperty("reserved_quantity")
    private Integer reservedQuantity;
}
