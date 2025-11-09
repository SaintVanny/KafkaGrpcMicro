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
        responseCode = "503",
        description = "Service temporarily unavailable",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                        value = """
                        {
                          "status": 503,
                          "error": "Service Unavailable",
                          "message": "Inventory Service isn't in work now. Try latter.",
                          "timestamp": "2025-01-15T10:30:00"
                        }
                        """
                )
        )
)
public @interface ApiServiceUnavailable {
    String value() default "Service temporarily unavailable";
}