package com.vanna.orders_serviceApp.controller;

import com.vanna.orders_serviceApp.annotation.swagger.authAnnotation.GetCurrentUserOperation;
import com.vanna.orders_serviceApp.annotation.swagger.authAnnotation.LoginUserOperation;
import com.vanna.orders_serviceApp.annotation.swagger.authAnnotation.RegisterUserOperation;
import com.vanna.orders_serviceApp.dto.users.AuthResponse;
import com.vanna.orders_serviceApp.dto.users.LoginRequest;
import com.vanna.orders_serviceApp.dto.users.RegisterRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login and profile information")
public class AuthController {

    private final AuthService authService;

    @RegisterUserOperation
    @PostMapping("/reg")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @LoginUserOperation
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetCurrentUserOperation
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}