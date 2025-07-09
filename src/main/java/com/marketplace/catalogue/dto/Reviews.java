package com.marketplace.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {
    private Double averageRating;
    private Integer count;
    private List<ReviewItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewItem {
        private String comment;
        private Double rating;
        private String userId;
        private LocalDateTime createdAt;
    }
}
