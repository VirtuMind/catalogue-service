package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetronomeInventoryRequest {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("quantity")
    private Integer quantity;
}
