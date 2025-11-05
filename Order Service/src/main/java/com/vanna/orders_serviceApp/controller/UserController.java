package com.vanna.orders_serviceApp.controller;

import com.vanna.orders_serviceApp.annotation.swagger.userAnnotation.DeleteUserOperation;
import com.vanna.orders_serviceApp.annotation.swagger.userAnnotation.GetAllUsersOperation;
import com.vanna.orders_serviceApp.dto.MessageResponse;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management operations (ADMIN only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetAllUsersOperation
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteUserOperation
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable UUID id) {
        String message = userService.deleteUser(id);
        MessageResponse response = MessageResponse.builder()
                .message(message)
                .build();
        return ResponseEntity.ok(response);
    }
}