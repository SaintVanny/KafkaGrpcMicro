package com.vanna.orders_serviceApp.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item in request (product + quantity)")
public class OrderItemRequest {

    @Schema(
            description = "Product ID to order",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Schema(
            description = "Quantity of this product",
            example = "5",
            minimum = "1",
            maximum = "1000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity must not exceed 1000")
    private Integer quantity;
}