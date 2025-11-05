package com.vanna.orders_serviceApp.controller;

import com.vanna.orders_serviceApp.annotation.swagger.orderAnnotation.*;
import com.vanna.orders_serviceApp.dto.MessageResponse;
import com.vanna.orders_serviceApp.dto.orders.CreateOrderRequest;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.dto.orders.UpdateOrderStatusRequest;
import com.vanna.orders_serviceApp.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management operations")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @CreateOrderOperation
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetCurrentUserOrdersOperation
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getCurrentUserOrders() {
        List<OrderResponse> orders = orderService.getCurrentUserOrders();
        return ResponseEntity.ok(orders);
    }

    @GetAllOrdersOperation
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @UpdateOrderStatusOperation
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteOrderOperation
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable UUID id) {
        String message = orderService.deleteOrder(id);
        MessageResponse response = MessageResponse.builder()
                .message(message)
                .build();
        return ResponseEntity.ok(response);
    }
}