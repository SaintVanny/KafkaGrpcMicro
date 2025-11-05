package com.vanna.orders_serviceApp.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(description = "Username (3-255 characters)", example = "newuser", minLength = 3, maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 255, message = "Username must be between 3 and 255 characters")
    private String username;

    @Schema(description = "Email address (optional, must be valid)", example = "user@example.com", maxLength = 255)
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "Password (min 6 characters)", example = "securepass123", minLength = 6, maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    private String password;
}