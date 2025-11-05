package com.vanna.orders_serviceApp.dto.users;

import com.vanna.orders_serviceApp.entity.enm.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information response")
public class UserResponse {

    @Schema(description = "User unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Username", example = "user1")
    private String username;

    @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN"})
    private UserRole role;

}