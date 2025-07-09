package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrnamentoDiscountResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("product_id")
    private Long productId;
    
    @JsonProperty("discount_percentage")
    private Double discountPercentage;
    
    @JsonProperty("start_date")
    private LocalDate startDate;
    
    @JsonProperty("end_date")
    private LocalDate endDate;
}
