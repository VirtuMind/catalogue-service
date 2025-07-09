package com.marketplace.catalogue.client;

import com.marketplace.catalogue.dto.external.MetronomeInventoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
public class MetronomeServiceClient {
    
    private final WebClient webClient;
    
    public MetronomeServiceClient(WebClient metronomeClient) {
        this.webClient = metronomeClient;
    }
    
    /**
     * Retrieves inventory count for a product from METRONOME service
     * @param productId the product ID
     * @return inventory count or null if service unavailable
     */
    public Integer getProductInventory(UUID productId) {
        try {
            MetronomeInventoryResponse response = webClient.get()
                    .uri("/inventory/{productId}", productId)
                    .retrieve()
                    .bodyToMono(MetronomeInventoryResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            return response != null ? response.getAvailableQuantity() : null;
        } catch (Exception e) {
            // Service unavailable, return null as specified in OpenAPI
            return null;
        }
    }
}
