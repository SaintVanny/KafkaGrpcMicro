package com.vanna.orders_serviceApp.dto.orders;

import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order information response with multiple products")
public class OrderResponse {

    @Schema(description = "Order unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Owner user ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID userId;

    @Schema(description = "Owner username", example = "user1")
    private String username;

    @Schema(description = "List of products in the order")
    @Builder.Default
    private List<OrderItemResponse> items = new ArrayList<>();

    @Schema(description = "Order name", example = "Office supplies order")
    private String orderName;

    @Schema(description = "Order status", example = "CREATED", allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED"})
    private OrderStatus status;

    @Schema(description = "Order creation timestamp", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Warnings about unavailable products (for partial fulfillment)")
    @Builder.Default
    private List<String> warnings = new ArrayList<>();
}