package com.vanna.orders_serviceApp.annotation.swagger.authAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiNotFound;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiUnauthorized;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Get current user profile",
        description = "Returns profile information of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "User profile retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserResponse.class)
                )
        )
})
@ApiUnauthorized
@ApiNotFound
public @interface GetCurrentUserOperation {
}
