package com.vanna.orders_serviceApp.service;

import com.vanna.orders_serviceApp.dto.users.AuthResponse;
import com.vanna.orders_serviceApp.dto.users.LoginRequest;
import com.vanna.orders_serviceApp.dto.users.RegisterRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;


public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser();
}
