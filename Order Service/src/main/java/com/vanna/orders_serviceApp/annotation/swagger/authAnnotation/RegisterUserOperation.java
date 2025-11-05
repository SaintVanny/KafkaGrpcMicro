package com.vanna.orders_serviceApp.annotation.swagger.authAnnotation;

import com.vanna.orders_serviceApp.annotation.swagger.common.ApiBadRequest;
import com.vanna.orders_serviceApp.annotation.swagger.common.ApiConflict;
import com.vanna.orders_serviceApp.dto.users.AuthResponse;
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
        summary = "Register new user",
        description = "Creates new user with USER role and returns JWT token"
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "User registered successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponse.class)
                )
        )
})
@ApiConflict
@ApiBadRequest
public @interface RegisterUserOperation {
}