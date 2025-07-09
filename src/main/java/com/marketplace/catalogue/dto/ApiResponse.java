package com.marketplace.catalogue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;

    // Success response with data
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data);
    }

    // Success response without data
    public static <T> ApiResponse<T> success(int status, String message) {
        return new ApiResponse<>(true, status, message, null);
    }

    // Error response with data
    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(false, status, message, data);
    }

    // Error response without data
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, status, message, null);
    }

    // Convenience methods for common HTTP status codes
    public static <T> ApiResponse<T> ok(String message, T data) {
        return success(200, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return success(200, "Données récupérées avec succès", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return success(201, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return success(201, "Ressource créée avec succès", data);
    }

    public static <T> ApiResponse<T> noContent(String message) {
        return success(204, message, null);
    }

    public static <T> ApiResponse<T> noContent() {
        return success(204, "Suppression effectuée avec succès", null);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message, null);
    }

    public static <T> ApiResponse<T> badRequest(String message, T errors) {
        return error(400, message, errors);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(401, message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message, null);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return error(409, message, null);
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return error(500, message, null);
    }

    public static <T> ApiResponse<T> serviceUnavailable(String message) {
        return error(503, message, null);
    }
}
