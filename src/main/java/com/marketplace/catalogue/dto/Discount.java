package com.marketplace.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    private Double discountPercentage;
    private Double discountPrice;
    private LocalDate startDate;
    private LocalDate endDate;
}
