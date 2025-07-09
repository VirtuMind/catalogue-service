package com.marketplace.catalogue.client;

import com.marketplace.catalogue.dto.Discount;
import com.marketplace.catalogue.dto.external.OrnamentoDiscountResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
public class OrnamentoServiceClient {
    
    private final WebClient webClient;
    
    public OrnamentoServiceClient(WebClient ornamentoClient) {
        this.webClient = ornamentoClient;
    }
    
    /**
     * Retrieves discount information for a product from ORNAMENTO service
     * @param productId the product ID
     * @return Discount object or null if service unavailable or no discount
     */
    public Discount getProductDiscount(UUID productId) {
        try {
            OrnamentoDiscountResponse response = webClient.get()
                    .uri("/promotions/{productId}", productId)
                    .retrieve()
                    .bodyToMono(OrnamentoDiscountResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            if (response == null) {
                return null;
            }
            
            // Map external DTO to internal DTO
            // Note: discountPrice is not provided by ORNAMENTO, it would need to be calculated
            return new Discount(
                    response.getDiscountPercentage(),
                    null, // discountPrice - would need to be calculated based on base price
                    response.getStartDate(),
                    response.getEndDate()
            );
        } catch (Exception e) {
            // Service unavailable, return null as specified in OpenAPI
            return null;
        }
    }
}
