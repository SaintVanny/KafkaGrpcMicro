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
        responseCode = "400",
        description = "Invalid request data or product unavailable",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                        @ExampleObject(
                                name = "Validation Error",
                                value = """
                                {
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Validation failed",
                                  "timestamp": "2025-10-14T17:42:15.465Z"
                                }
                                """
                        ),
                        @ExampleObject(
                                name = "Product Unavailable",
                                value = """
                                {
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Product is not available. Requested: 10, Available: 3. Out of stock",
                                  "timestamp": "2025-01-15T10:30:00"
                                }
                                """
                        )
                }
        )
)
public @interface ApiBadRequest {
    String value() default "Invalid request data";
}
