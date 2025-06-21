package com.marketplace.catalogue.controller;

import com.marketplace.catalogue.config.GlobalExceptionHandler.ApiError;
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
    public ResponseEntity<ApiError> handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        if (status == 404) {
            return ResponseEntity.status(404).body(new ApiError(404, "Resource not found - check the URL for a typo or a missing parameter"));
        }

        return ResponseEntity.status(status).body(new ApiError(status, "An unexpected error occurred: " + request.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
    }
}
