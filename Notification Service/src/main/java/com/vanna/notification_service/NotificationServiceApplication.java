package com.vanna.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for Notification Service.
 *
 * This service:
 * 1. Consumes order events from Kafka (topic "orders")
 * 2. Stores data in denormalized PostgreSQL table
 * 3. Provides read-only REST API for analytics
 *
 * Port: 8082
 * Swagger UI: http://localhost:8081/swagger-ui.html
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}