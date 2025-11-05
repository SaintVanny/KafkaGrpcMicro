package com.vanna.orders_serviceApp.dto.orders;

import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update order status")
public class UpdateOrderStatusRequest {

    @Schema(description = "New order status", example = "IN_PROGRESS", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"CREATED", "IN_PROGRESS", "COMPLETED"})
    @NotNull(message = "Status is required")
    private OrderStatus status;
}