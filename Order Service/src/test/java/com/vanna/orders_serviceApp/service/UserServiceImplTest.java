package com.vanna.orders_serviceApp.service;

import com.vanna.orders_serviceApp.dto.users.UpdateUserRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.mapper.UserMapper;
import com.vanna.orders_serviceApp.repository.UserRepository;
import com.vanna.orders_serviceApp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        testUserResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();
    }

    // ========== getUserById Tests ==========

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse result = userService.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(nonExistentId))
                .isInstanceOf(RestOrdersException.class)
                .hasMessageContaining("User not found with id: " + nonExistentId)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById(nonExistentId);
        verify(userMapper, never()).toResponse(any());
    }

    // ========== updateUser Tests ==========

    @Test
    void updateUser_shouldUpdateEmail_whenEmailIsUniqueAndValid() {
        // Given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("newemail@example.com")
                .build();

        User currentAdmin = User.builder()
                .id(UUID.randomUUID())  // Different from testUser
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("newemail@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        UserResponse updatedResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("newemail@example.com")
                .role(UserRole.USER)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentAdmin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        // When
        UserResponse result = userService.updateUser(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newemail@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userMapper).updateUserFromDto(request, testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_shouldUpdateRole_whenUserIsNotSelf() {
        // Given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .role(UserRole.ADMIN)
                .build();

        User currentAdmin = User.builder()
                .id(UUID.randomUUID())  // Different from testUser
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .build();

        UserResponse updatedResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.ADMIN)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentAdmin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        // When
        UserResponse result = userService.updateUser(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);

        verify(userMapper).updateUserFromDto(request, testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_shouldThrowConflictException_whenEmailAlreadyExists() {
        // Given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("existing@example.com")
                .build();

        User currentAdmin = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentAdmin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(RestOrdersException.class)
                .hasMessageContaining("Email already in use")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowForbiddenException_whenAdminTriesToChangeOwnRole() {
        // Given
        User adminUser = User.builder()
                .id(userId)
                .username("admin")
                .email("admin@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .role(UserRole.USER)  // Trying to downgrade to USER
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(RestOrdersException.class)
                .hasMessageContaining("Cannot change your own role")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("newemail@example.com")
                .build();

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(nonExistentId, request))
                .isInstanceOf(RestOrdersException.class)
                .hasMessageContaining("User not found with id: " + nonExistentId)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldNotCheckEmailUniqueness_whenEmailIsNotChanged() {
        // Given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("test@example.com")  // Same as current email
                .build();

        User currentAdmin = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentAdmin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userRepository, never()).existsByEmail(anyString());
    }
}