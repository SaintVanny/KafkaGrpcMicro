package com.vanna.notification_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestNotificationException extends RuntimeException {

    private final HttpStatus status;

    public RestNotificationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}