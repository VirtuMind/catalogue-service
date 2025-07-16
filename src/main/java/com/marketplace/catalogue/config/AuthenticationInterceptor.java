package com.marketplace.catalogue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.catalogue.client.AriaServiceClient;
import com.marketplace.catalogue.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    private final AriaServiceClient ariaServiceClient;
    private final ObjectMapper objectMapper;
    private final TokenHolder tokenHolder;


    public AuthenticationInterceptor(AriaServiceClient ariaServiceClient, ObjectMapper objectMapper, TokenHolder tokenHolder) {
        this.ariaServiceClient = ariaServiceClient;
        this.objectMapper = objectMapper;
        this.tokenHolder = tokenHolder;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip authentication for GET requests (as per OpenAPI spec)
        if (HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }
        
        // Skip authentication for OPTIONS requests (CORS preflight)
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // Clear previous token
        tokenHolder.clear();
        
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, ApiResponse.unauthorized("En-tête d'autorisation manquant ou invalide"));
            return false;
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        // Validate token with ARIA service
        boolean isValid = ariaServiceClient.validateToken(token);
        if (!isValid) {
            sendErrorResponse(response, ApiResponse.unauthorized("Token invalide ou expiré"));
            return false;
        }

        // Store the token for other services to use
        tokenHolder.setToken(token);

        return true;
    }
    
    private void sendErrorResponse(HttpServletResponse response, ApiResponse<Void> apiResponse) throws Exception {
        response.setStatus(apiResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
    }
}
