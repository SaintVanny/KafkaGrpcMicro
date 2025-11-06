package com.vanna.orders_serviceApp.dto.users;

import com.vanna.orders_serviceApp.entity.enm.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User update request (ADMIN only)")
public class UpdateUserRequest {

    @Email(message = "Email should be valid")
    @Schema(description = "New email address", example = "newemail@example.com")
    private String email;

    @Schema(description = "New user role", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
    private UserRole role;
}