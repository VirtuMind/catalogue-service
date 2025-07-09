package com.marketplace.catalogue.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EchoReviewResponse {
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("produitId")
    private String produitId;
    
    @JsonProperty("commentaire")
    private String commentaire;
    
    @JsonProperty("note")
    private Integer note;
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("date")
    private LocalDateTime date;
}
