package com.vanna.orders_serviceApp.annotation.swagger.orderAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiForbidden;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiUnauthorized;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
        summary = "Get all orders (ADMIN only)",
        description = "Returns all orders in the system. Requires ADMIN role"
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "All orders retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(
                                schema = @Schema(implementation = OrderResponse.class)
                        )
                )
        )
})
@ApiUnauthorized
@ApiForbidden
public @interface GetAllOrdersOperation {
}
