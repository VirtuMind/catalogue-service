package com.marketplace.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private String reviewerName;
    private String comment;
    private Integer rating;
    private String date;
}
