package com.vanna.orders_serviceApp.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login credentials")
public class LoginRequest {

    @Schema(description = "Username or Email address", example = "user1 or user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username or Email is required")
    private String username;

    @Schema(description = "Password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    private String password;
}