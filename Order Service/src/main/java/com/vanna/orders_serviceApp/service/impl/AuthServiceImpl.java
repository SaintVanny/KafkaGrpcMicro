package com.vanna.orders_serviceApp.service.impl;

import com.vanna.orders_serviceApp.config.JwtUtil;
import com.vanna.orders_serviceApp.dto.users.AuthResponse;
import com.vanna.orders_serviceApp.dto.users.LoginRequest;
import com.vanna.orders_serviceApp.dto.users.RegisterRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.mapper.UserMapper;
import com.vanna.orders_serviceApp.repository.UserRepository;
import com.vanna.orders_serviceApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("User registration attempt: username={}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: username={}", request.getUsername());
            throw new RestOrdersException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: userId={}, username={}, role={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getRole());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("User login attempt: username={}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: username={}", request.getUsername());
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password: username={}", request.getUsername());
            throw new RestOrdersException(HttpStatus.FORBIDDEN, "Invalid password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        log.info("User logged in successfully: username={}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Fetching current user info: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Current authenticated user not found in database: username={}", username);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "User not found");
                });

        return userMapper.toResponse(user);
    }
}
