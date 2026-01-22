package com.vanna.notification_service.controller;

import com.vanna.notification_service.dto.notifications.OrderNotificationResponse;
import com.vanna.notification_service.exception.RestNotificationException;
import com.vanna.notification_service.service.OrderNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders Analytics", description = "Read-only API for Analytics ")
public class OrderNotificationController {

    private final OrderNotificationService service;
    
    @GetMapping("/all")
    @Operation(summary = "Get all orders", description = "Return all positions in all orders in the system")
    public ResponseEntity<List<OrderNotificationResponse>> getAllOrders() {
        log.debug("REST: Request all Orders");

        List<OrderNotificationResponse> orders = service.getAllOrders();

        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get Order by ID", description = "Return all positions in target order")
    public ResponseEntity<List<OrderNotificationResponse>> getOrderByOrderId(
            @PathVariable UUID orderId) {
        log.debug("REST: request order orderId={}", orderId);

        List<OrderNotificationResponse> orders = service.getOrdersByOrderId(orderId);

        if (orders.isEmpty()) {
            throw new RestNotificationException(HttpStatus.NOT_FOUND,
                    "Order not found with id: " + orderId);
        }

        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Return all positions of all orders by user")
    public ResponseEntity<List<OrderNotificationResponse>> getOrdersByUserId(
            @PathVariable UUID userId) {
        log.debug("REST: request of orders by user userId={}", userId);

        List<OrderNotificationResponse> orders = service.getOrdersByUserId(userId);

        return ResponseEntity.ok(orders);
    }
}