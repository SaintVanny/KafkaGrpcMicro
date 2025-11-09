package com.vanna.orders_serviceApp.service.impl;

import com.vanna.orders_serviceApp.dto.users.UpdateUserRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.mapper.UserMapper;
import com.vanna.orders_serviceApp.repository.UserRepository;
import com.vanna.orders_serviceApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users (admin operation)");

        List<User> users = userRepository.findAll();
        log.info("Found {} users in system", users.size());

        return userMapper.toResponseList(users);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user by ID: userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: userId={}", id);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
                });

        log.debug("User found: userId={}, username={}", user.getId(), user.getUsername());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.debug("Attempting to update user: userId={}, updateData={}", id, request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update: userId={}", id);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
                });
        User currentUser = getCurrentAuthenticatedUser();

        boolean isSelfEdit = currentUser.getId().equals(id);
        boolean roleChanged = request.getRole() != currentUser.getRole() && request.getRole() != null;
        if (isSelfEdit && roleChanged) {
            log.warn("Admin attempting to change own role: userId={}, currentRole={}, requestedRole={}",
                    id, user.getRole(), request.getRole());
            throw new RestOrdersException(HttpStatus.FORBIDDEN, "Cannot change your own role");
        }

        boolean emailChanged = request.getEmail() != null && !request.getEmail().equals(user.getEmail());
        if (emailChanged && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already in use: email={}", request.getEmail());
            throw new RestOrdersException(HttpStatus.CONFLICT, "Email already in use: " +
                    request.getEmail());
        }
        userMapper.updateUserFromDto(request, user);

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: userId={}, username={}, newEmail={}, newRole={}",
                updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getRole());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public String deleteUser(UUID id) {
        log.debug("Attempting to delete user: userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion: userId={}", id);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
                });

        String username = user.getUsername();
        userRepository.deleteById(id);
        log.info("User deleted successfully: userId={}, username={}", id, username);

        return "User '" + username + "' (ID: " + id + ") has been successfully deleted";
    }

    @Override
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching authenticated user: username={}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Authenticated user not found in database: username={}", username);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found: " + username);
                });
    }
}
