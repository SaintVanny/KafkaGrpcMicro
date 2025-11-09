package com.vanna.orders_serviceApp.annotation.swagger.orderAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiBadRequest;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiServiceUnavailable;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiUnauthorized;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Create new order",
        description = "Creates a new order for the authenticated user. Checks product availability in Inventory Service via gRPC before creation."
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Order created successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderResponse.class),
                        examples = @ExampleObject(
                                name = "Created Order",
                                value = """
                                {
                                  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa7",
                                  "username": "john_doe",
                                  "productId": "123e4567-e89b-12d3-a456-426614174000",
                                  "quantity": 5,
                                  "orderName": "Office laptops order",
                                  "status": "CREATED",
                                  "createdAt": "2025-01-15T10:30:00"
                                }
                                """
                        )
                )
        )
})
@ApiUnauthorized
@ApiBadRequest
@ApiServiceUnavailable
public @interface CreateOrderOperation {
}
