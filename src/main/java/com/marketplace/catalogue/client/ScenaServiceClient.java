package com.marketplace.catalogue.client;

import com.marketplace.catalogue.dto.external.ScenaMediaItemResponse;
import com.marketplace.catalogue.dto.external.ScenaUploadRequest;
import com.marketplace.catalogue.dto.external.ScenaUploadResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
public class ScenaServiceClient {
    
    private final WebClient webClient;
    
    public ScenaServiceClient(WebClient scenaClient) {
        this.webClient = scenaClient;
    }
    
    /**
     * Uploads a thumbnail file for a product
     * @param file the thumbnail file
     * @param productId the product ID
     * @return ScenaUploadResponse or null if service unavailable
     */
    public ScenaUploadResponse uploadThumbnail(MultipartFile file, UUID productId) {
        try {
            // Create multipart body for file upload
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", file.getResource());
            builder.part("product_id", productId.toString());
            
            // Make API call to SCENA service
            ScenaUploadResponse response = webClient.post()
                    .uri("/upload/thumbnail")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(ScenaUploadResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return response;
        } catch (Exception e) {
            // Service unavailable, return null
            return null;
        }
    }

    /**
     * Uploads a single media file for a product
     * @param file the media file
     * @param productId the product ID
     * @return ScenaUploadResponse or null if service unavailable
     */
    public ScenaUploadResponse uploadMediaFile(MultipartFile file, UUID productId) {
        try {
            // Create multipart body for file upload
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", file.getResource());
            builder.part("product_id", productId.toString());
            
            // Make API call to SCENA service
            ScenaUploadResponse response = webClient.post()
                    .uri("/upload/media")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(ScenaUploadResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return response;
        } catch (Exception e) {
            // Service unavailable, return null
            return null;
        }
    }

    /**
     * Gets the thumbnail URL for a product
     * @param productId the product ID
     * @return thumbnail URL or null if service unavailable or no thumbnail
     */
    public String getThumbnailUrl(UUID productId) {
        try {
            // Make API call to SCENA service
            ScenaMediaItemResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/thumbnail")
                            .queryParam("id_product", productId)
                            .build())
                    .retrieve()
                    .bodyToMono(ScenaMediaItemResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
                    
            return response != null ? response.getFileUrl() : null;
        } catch (Exception e) {
            // Service unavailable, return null
            return null;
        }
    }
    

    /**
     * Retrieves all media URLs for a product (excluding thumbnail)
     * @param productId the product ID
     * @return list of media URLs or null if service unavailable
     */
    public List<String> getProductMediaUrls(UUID productId) {
        try {
            // Make API call to SCENA service
            List<ScenaMediaItemResponse> response = webClient.get()
                    .uri((uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_product", productId)
                            .build()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ScenaMediaItemResponse>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();
                    
            return response != null ? response.stream()
                    .map(ScenaMediaItemResponse::getFileUrl)
                    .toList() : null;
        } catch (Exception e) {
            // Service unavailable, return null
            return null;
        }
    }

    /**
     * Retrieves all media Ids for a product
     * @param productId the product ID
     * @return list of media URLs or null if service unavailable
     */
    public List<String> getProductMediaIds(UUID productId) {
        try {
            // Make API call to SCENA service
            List<ScenaMediaItemResponse> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_product", productId)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ScenaMediaItemResponse>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return response != null ? response.stream()
                    .map(ScenaMediaItemResponse::getId)
                    .toList() : null;
        } catch (Exception e) {
            // Service unavailable, return null
            return null;
        }
    }

    /**
     * Deletes the thumbnail for a product
     * @param productId
     * @return true if successful, false if service unavailable or error
     */
    public boolean deleteThumbnail(UUID productId) {
        try {
            webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/thumbnail")
                            .queryParam("id_product", productId)
                            .build())
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
     * Deletes the thumbnail for a product
     * @param mediaId
     * @return true if successful, false if service unavailable or error
     */
    public boolean deleteMedia(String mediaId) {
        try {
            webClient.delete()
                    .uri((uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_media", mediaId)
                            .build()))
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
