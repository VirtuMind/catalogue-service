package com.marketplace.catalogue.client;

import com.marketplace.catalogue.dto.external.AriaTokenValidationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Component
public class AriaServiceClient {
    
    private final WebClient webClient;
    
    public AriaServiceClient(WebClient ariaClient) {
        this.webClient = ariaClient;
    }
    
    /**
     * Validates JWT token with ARIA authentication service
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            AriaTokenValidationResponse response = webClient
                    .get()
                    .uri("/users/validate")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(AriaTokenValidationResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return response != null && response.isValid() && response.getRole().equals("admin");
        } catch (WebClientResponseException e) {
            // Token validation failed
            return false;
        } catch (Exception e) {
            // Service unavailable - return false for security
            return false;
        }
    }
}
