package com.vanna.orders_serviceApp.annotation.swagger.userAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiBadRequest;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiConflict;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiForbidden;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiNotFound;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiUnauthorized;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
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
        summary = "Update user (ADMIN only)",
        description = "Updates user email and/or role. Admin cannot change own role. Requires ADMIN role"
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponse.class)
                )
        )
})
@ApiUnauthorized
@ApiForbidden
@ApiNotFound
@ApiConflict
@ApiBadRequest
public @interface UpdateUserOperation {
}