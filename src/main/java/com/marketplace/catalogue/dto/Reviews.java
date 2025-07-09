package com.marketplace.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    private Double averageRating;
    private Integer count;
    private List<Items> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Items {
        private UUID userId;
        private String comment;
        private Integer rating;
        private String date;
    }

}
