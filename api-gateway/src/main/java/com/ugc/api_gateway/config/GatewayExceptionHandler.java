package com.ugc.api_gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(TooManyRequestsException.class)
    public org.springframework.http.ResponseEntity<Map<String, Object>> handleTooManyRequests(
            TooManyRequestsException ex) {

        return org.springframework.http.ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                        "status", 429,
                        "error", "Too Many Requests",
                        "message", ex.getMessage()
                ));
    }
}