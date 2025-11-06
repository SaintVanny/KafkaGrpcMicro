package com.vanna.orders_serviceApp.service;

import com.vanna.orders_serviceApp.dto.users.UpdateUserRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    String deleteUser(UUID id);

    User getCurrentAuthenticatedUser();
}
