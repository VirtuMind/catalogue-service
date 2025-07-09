package com.marketplace.catalogue.config;

import com.marketplace.catalogue.dto.ApiResponse;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = "Méthode non autorisée: " + ex.getMethod() + 
                        ". Méthodes supportées: " + ex.getSupportedHttpMethods();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(405, message));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String message = "Type de média non supporté: " + ex.getContentType() + 
                        ". Types supportés: " + ex.getSupportedMediaTypes();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error(415, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Valeur invalide '%s' pour le paramètre '%s'. Type attendu: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::buildValidationErrorMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest("Données d'entrée invalides", errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        String message = "La taille du fichier dépasse la limite autorisée: " + ex.getMaxUploadSize() + " octets";
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error(413, message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Violation de l'intégrité des données";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Entrée dupliquée: Un enregistrement avec ces informations existe déjà";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "Violation de contrainte de clé étrangère: L'entité référencée n'existe pas";
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.conflict(message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        // Check if it's a service unavailable exception
        if (ex.getMessage().contains("service") || ex.getMessage().contains("Service")) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.serviceUnavailable("Service externe temporairement indisponible: " + ex.getMessage()));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.internalServerError("Une erreur interne du serveur s'est produite: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.internalServerError("Une erreur inattendue s'est produite: " + ex.getMessage()));
    }

    private String buildValidationErrorMessage(FieldError fieldError) {
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        Object rejectedValue = fieldError.getRejectedValue();

        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Champ '").append(field).append("': ");

        // Use the default message if available, otherwise build custom message
        if (message != null && !message.isEmpty()) {
            errorMsg.append(message);
        } else {
            errorMsg.append("valeur invalide");
        }

        // Add rejected value if available
        if (rejectedValue != null) {
            errorMsg.append(" (reçu: '").append(rejectedValue).append("')");
        } else {
            errorMsg.append(" (reçu: null)");
        }

        return errorMsg.toString();
    }
}

