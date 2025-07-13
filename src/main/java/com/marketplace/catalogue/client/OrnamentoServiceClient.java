package com.marketplace.catalogue.client;

import com.marketplace.catalogue.dto.Discount;
import com.marketplace.catalogue.dto.external.OrnamentoDiscountRequest;
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

    /**
     * Get promotion ID for a product from ORNAMENTO service
     * @param productId the product ID
     * @return promotion ID or null if service unavailable or no promotion
     */
    public Long getProductDiscountId(UUID productId) {
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

            return response.getId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Adds a new discount for a new product in ORNAMENTO service
     * @param request the discount request object
     * @return true if successful, false if service unavailable or error
     */

    public boolean addNewProductDiscount(OrnamentoDiscountRequest request) {
        try {
            webClient.post()
                    .uri("/promotions")
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
     * Updates an existing discount for a product in ORNAMENTO service
     * @param promotionId the product ID
     * @param request the discount request object
     * @return true if successful, false if service unavailable or error
     */
    public boolean updateProductDiscount(String promotionId, OrnamentoDiscountRequest request) {
        try {
            webClient.put()
                    .uri("/promotions/{promotionId}", promotionId)
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
     * Deletes a discount for a product in ORNAMENTO service
     * @param promotionId the product ID
     * @return true if successful, false if service unavailable or error
     */
    public boolean deleteProductDiscount(String promotionId) {
        try {
            webClient.delete()
                    .uri("/promotions/{promotionId}", promotionId)
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
