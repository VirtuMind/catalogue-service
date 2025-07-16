package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenaMediaItemResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("product_id")
    private String productId;
    
    @JsonProperty("file_url")
    private String fileUrl;
    
    @JsonProperty("file_type")
    private String fileType; // "image" or "video"

    @JsonProperty("is_thumbnail")
    private Boolean is_thumbnail;
}
