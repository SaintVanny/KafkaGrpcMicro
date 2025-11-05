package com.vanna.orders_serviceApp.annotation.swagger.common;

import com.vanna.orders_serviceApp.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "401",
        description = "Authentication required - invalid or missing JWT token",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                        value = """
                        {
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Authentication required - invalid or missing JWT token",
                          "timestamp": "2025-10-14T17:42:15.465Z"
                        }
                        """
                )
        )
)
public @interface ApiUnauthorized {
    String value() default "Authentication required - invalid or missing JWT token";
}
