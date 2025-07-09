package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.dto.ApiResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {
    @RequestMapping
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        if (status == 404) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("Ressource non trouvée - vérifiez l'URL pour une faute de frappe ou un paramètre manquant"));
        }

        String errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE) != null 
                ? request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString() 
                : "Erreur inconnue";
        
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status, "Une erreur inattendue s'est produite: " + errorMessage));
    }
}
