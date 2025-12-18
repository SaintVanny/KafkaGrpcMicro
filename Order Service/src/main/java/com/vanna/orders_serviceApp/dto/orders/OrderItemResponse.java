package com.vanna.orders_serviceApp.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order item response (product + quantity)")
public class OrderItemResponse {

    @Schema(description = "Product ID", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID productId;

    @Schema(description = "Quantity of this product", example = "5")
    private Integer quantity;
}