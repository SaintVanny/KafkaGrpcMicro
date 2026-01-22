package com.vanna.notification_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RestNotificationException.class)
    public ResponseEntity<ErrorResponse> handleRestNotificationException(RestNotificationException ex) {
        log.warn("exception in logic : status={}, message={}", ex.getStatus(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(
            ex.getStatus().value(),
            ex.getMessage(),
            LocalDateTime.now()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexcepted exception: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
            500,
            "Internal server error",  
            LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}