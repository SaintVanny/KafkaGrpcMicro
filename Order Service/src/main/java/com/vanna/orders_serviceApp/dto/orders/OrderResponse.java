package com.vanna.orders_serviceApp.dto.orders;

import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order information response")
public class OrderResponse {

    @Schema(description = "Order unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Owner user ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID userId;

    @Schema(description = "Owner username", example = "user1")
    private String username;

    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID productId;

    @Schema(description = "Quantity of products", example = "5")
    private Integer quantity;

    @Schema(description = "Order name", example = "Office laptops order")
    private String orderName;

    @Schema(description = "Order status", example = "CREATED", allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED"})
    private OrderStatus status;

    @Schema(description = "Order creation timestamp", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;
}