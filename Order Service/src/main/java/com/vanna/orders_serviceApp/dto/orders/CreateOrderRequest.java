package com.vanna.orders_serviceApp.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
public class CreateOrderRequest {

    @Schema(
            description = "Product ID to order",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @Schema(
            description = "Quantity of products",
            example = "5",
            minimum = "1",
            maximum = "1000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity must not exceed 1000")
    private Integer quantity;
    
    @Schema(
            description = "Order name (1-1000 characters)",
            example = "Office laptops order",
            minLength = 1,
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Order name is required")
    @Size(min = 1, max = 1000, message = "Order name must be between 1 and 1000 characters")
    private String orderName;
}