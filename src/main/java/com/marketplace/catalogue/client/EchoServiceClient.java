package com.marketplace.catalogue.client;

import com.marketplace.catalogue.config.TokenHolder;
import com.marketplace.catalogue.dto.Reviews;
import com.marketplace.catalogue.dto.external.EchoReviewResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
public class EchoServiceClient {
    
    private final WebClient webClient;
    private final TokenHolder tokenHolder;
    
    public EchoServiceClient(WebClient echoClient, TokenHolder tokenHolder) {
        this.webClient = echoClient;
        this.tokenHolder = tokenHolder;
    }
    
    /**
     * Retrieves reviews for a product from ECHO service
     * @param productId the product ID
     * @return Reviews object or null if service unavailable
     */
    public Reviews getProductReviews(UUID productId) {
        try {
            String token = tokenHolder.getToken();
            List<EchoReviewResponse> response = webClient.get()
                    .uri("/avis/{productId}", productId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<EchoReviewResponse>>() {})
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            if (response == null || response.isEmpty()) {
                return null;
            }
            
            // Map external DTO to internal DTO
            List<Reviews.ReviewItem> items = response.stream()
                    .map(echoReview -> new Reviews.ReviewItem(
                            echoReview.getCommentaire(),
                            echoReview.getNote().doubleValue(),
                            echoReview.getUserId(),
                            echoReview.getDate()
                    ))
                    .toList();
                    
            double averageRating = response.stream()
                    .mapToInt(EchoReviewResponse::getNote)
                    .average()
                    .orElse(0.0);
                    
            return new Reviews(averageRating, response.size(), items);
        } catch (Exception e) {
            // Service unavailable, return null as specified in OpenAPI
            return null;
        }
    }
}
