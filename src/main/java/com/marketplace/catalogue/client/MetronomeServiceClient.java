package com.marketplace.catalogue.client;

import com.marketplace.catalogue.config.TokenHolder;
import com.marketplace.catalogue.dto.external.MetronomeInventoryRequest;
import com.marketplace.catalogue.dto.external.MetronomeInventoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
public class MetronomeServiceClient {

    private final WebClient webClient;
    private final TokenHolder tokenHolder;

    public MetronomeServiceClient(WebClient metronomeClient, TokenHolder tokenHolder) {
        this.webClient = metronomeClient;
        this.tokenHolder = tokenHolder;
    }

    /**
     * Retrieves inventory count for a product from METRONOME service
     *
     * @param productId the product ID
     * @return inventory count or null if service unavailable
     */
    public Integer getProductInventory(UUID productId) {
        try {
            String token = tokenHolder.getToken();
            MetronomeInventoryResponse response = webClient.get()
                    .uri("/inventory/{productId}", productId)
                    .header("Authorization", "Bearer " + token)
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

    /**
     * Adds inventory for a new product in METRONOME service
     *
     * @param request the inventory request object
     * @return true if successful, false if service unavailable or error
     */
    public boolean increaseProductInventory(MetronomeInventoryRequest request) {
        try {
            String token = tokenHolder.getToken();
            webClient.post()
                    .uri("/inventory/add")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Decreases inventory for a product in METRONOME service
     *
     * @param request the inventory request object
     * @return true if successful, false if service unavailable or error
     */
    public boolean decreaseProductInventory(MetronomeInventoryRequest request) {
        try {
            String token = tokenHolder.getToken();
            webClient.post()
                    .uri("/inventory/decrease")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}