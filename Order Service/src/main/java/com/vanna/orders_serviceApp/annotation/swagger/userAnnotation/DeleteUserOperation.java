package com.vanna.orders_serviceApp.annotation.swagger.userAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiForbidden;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiNotFound;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiUnauthorized;
import com.vanna.orders_serviceApp.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
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
        summary = "Delete user (ADMIN only)",
        description = "Deletes a user by ID. Requires ADMIN role"
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "User deleted successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponse.class)
                )
        )
})
@ApiUnauthorized
@ApiForbidden
@ApiNotFound
public @interface DeleteUserOperation {
}
