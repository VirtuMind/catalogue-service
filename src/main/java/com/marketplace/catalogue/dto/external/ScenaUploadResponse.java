package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenaUploadResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("file_url")
    private String fileUrl;
    
    @JsonProperty("product_id")
    private String productId;
    
    @JsonProperty("file_type")
    private String fileType; // "image" or "video"
}
