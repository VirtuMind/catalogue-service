package com.marketplace.catalogue.config;

import jakarta.validation.constraints.*;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(int status, String message, List<String> errors) {
        public ApiError(int status, String message) {
            this(status, message, null);
        }
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Resource found but Method not allowed: " + ex.getMessage() + ", if the method is correct, check the request URL.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s.",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::buildValidationErrorMessage)
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input data",
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }

    private String buildValidationErrorMessage(FieldError fieldError) {
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        Object rejectedValue = fieldError.getRejectedValue();

        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Field '").append(field).append("': ");

        // Handle specific validation annotations
        if (fieldError.contains(NotNull.class)) {
            errorMsg.append("must not be null");
        } else if (fieldError.contains(NotBlank.class)) {
            errorMsg.append("must not be blank");
        } else if (fieldError.contains(NotEmpty.class)) {
            errorMsg.append("must not be empty");
        } else if (fieldError.contains(Size.class)) {
            errorMsg.append("size must be between ")
                    .append(fieldError.unwrap(Size.class).min())
                    .append(" and ")
                    .append(fieldError.unwrap(Size.class).max());
        } else if (fieldError.contains(Pattern.class)) {
            errorMsg.append("must match pattern: ")
                    .append(fieldError.unwrap(Pattern.class).regexp());
        } else if (fieldError.contains(Min.class)) {
            errorMsg.append("must be ≥ ")
                    .append(fieldError.unwrap(Min.class).value());
        } else if (fieldError.contains(Max.class)) {
            errorMsg.append("must be ≤ ")
                    .append(fieldError.unwrap(Max.class).value());
        } else {
            errorMsg.append(message != null ? message : "invalid value");
        }

        // Add rejected value if available
        if (rejectedValue != null) {
            errorMsg.append(" (received: '").append(rejectedValue).append("')");
        } else {
            errorMsg.append(" (received null)");
        }

        return errorMsg.toString();
    }


}

