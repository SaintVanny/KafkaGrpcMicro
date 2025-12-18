package com.vanna.orders_serviceApp.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order with multiple products")
public class CreateOrderRequest {

    @Schema(
            description = "List of products to order (1-100 items)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Items list cannot be empty")
    @Size(min = 1, max = 100, message = "Order must contain between 1 and 100 items")
    @Valid
    private List<OrderItemRequest> items;

    @Schema(
            description = "Order name (1-1000 characters)",
            example = "Office supplies order",
            minLength = 1,
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Order name is required")
    @Size(min = 1, max = 1000, message = "Order name must be between 1 and 1000 characters")
    private String orderName;
}