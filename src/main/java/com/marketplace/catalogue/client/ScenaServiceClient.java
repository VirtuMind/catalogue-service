package com.marketplace.catalogue.client;

import com.marketplace.catalogue.config.TokenHolder;
import com.marketplace.catalogue.dto.external.ScenaMediaItemResponse;
import com.marketplace.catalogue.dto.external.ScenaUploadRequest;
import com.marketplace.catalogue.dto.external.ScenaUploadResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
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
    private final TokenHolder tokenHolder;

    public ScenaServiceClient(WebClient scenaClient, TokenHolder tokenHolder) {
        this.webClient = scenaClient;
        this.tokenHolder =  tokenHolder;
    }


    /**
     * Uploads a single media file for a product
     * @param file the media file
     * @param productId the product ID
     * @return ScenaUploadResponse or null if service unavailable
     */
    public ScenaUploadResponse uploadMediaFile(MultipartFile file, UUID productId, boolean isThumbnail) {
        try {

            // Create a resource from the file instead of passing the MultipartFile directly
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // Create multipart body for file upload
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", fileResource)
                    .filename(file.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(file.getContentType()));
            builder.part("product_id", productId.toString());
            builder.part("is_thumbnail", String.valueOf(isThumbnail));

            String token = tokenHolder.getToken();

            // Make API call to SCENA service
            ScenaUploadResponse response = webClient.post()
                    .uri("/upload")
                    .header("Authorization", "Bearer " + token)
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
            String token = tokenHolder.getToken();

            // Make API call to SCENA service
            ScenaMediaItemResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/thumbnail")
                            .queryParam("id_product", productId)
                            .build())
                    .header("Authorization", "Bearer " + token)
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
     * Get Thumbnail ID for a product
     * @param productId the product ID
     * @return thumbnail ID or null if service unavailable or no thumbnail
     */

    public String getThumbnailId(UUID productId) {
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

            return response != null ? response.getId() : null;
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
            String token = tokenHolder.getToken();
            // Make API call to SCENA service
            List<ScenaMediaItemResponse> response = webClient.get()
                    .uri((uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_product", productId)
                            .build()))
                    .header("Authorization", "Bearer " + token)
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
            String token = tokenHolder.getToken();
            // Make API call to SCENA service
            List<ScenaMediaItemResponse> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_product", productId)
                            .build())
                    .header("Authorization", "Bearer " + token)
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
     * @param mediaId
     * @return true if successful, false if service unavailable or error
     */
    public boolean deleteMedia(String mediaId) {
        try {
            String token = tokenHolder.getToken();
            webClient.delete()
                    .uri((uriBuilder -> uriBuilder
                            .path("/media")
                            .queryParam("id_media", mediaId)
                            .build()))
                    .header("Authorization", "Bearer " + token)
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
