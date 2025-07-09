package com.marketplace.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails extends ProductMeta {
    private List<String> mediaUrls;
    
    private Discount discount;
    
    private Integer inventory;
    
    private Reviews reviews;
}
