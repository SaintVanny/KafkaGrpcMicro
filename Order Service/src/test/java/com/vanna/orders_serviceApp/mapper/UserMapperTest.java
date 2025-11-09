package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.users.UpdateUserRequest;
import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toResponse_shouldMapAllFields() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.USER)
                .build();

        UserResponse response = userMapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void toResponse_shouldMapAdminRole() {
        User admin = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        UserResponse response = userMapper.toResponse(admin);
        
        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void toResponse_shouldReturnNull_whenUserIsNull() {
        UserResponse response = userMapper.toResponse(null);
        assertThat(response).isNull();
    }


    @Test
    void updateUserFromDto_shouldUpdateEmail_whenEmailIsProvided() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("old@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("new@example.com")
                .build();

        userMapper.updateUserFromDto(request, user);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getUsername()).isEqualTo("testuser");  
        assertThat(user.getRole()).isEqualTo(UserRole.USER);  
        assertThat(user.getPassword()).isEqualTo("password");  
    }

    @Test
    void updateUserFromDto_shouldUpdateRole_whenRoleIsProvided() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .role(UserRole.ADMIN)
                .build();

        userMapper.updateUserFromDto(request, user);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getEmail()).isEqualTo("test@example.com");  
        assertThat(user.getUsername()).isEqualTo("testuser");  
    }

    @Test
    void updateUserFromDto_shouldUpdateBothEmailAndRole_whenBothProvided() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("old@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("new@example.com")
                .role(UserRole.ADMIN)
                .build();

        userMapper.updateUserFromDto(request, user);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getUsername()).isEqualTo("testuser");  
    }

    @Test
    void updateUserFromDto_shouldNotUpdateEmail_whenEmailIsNull() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("original@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .role(UserRole.ADMIN)
                .build();  

        userMapper.updateUserFromDto(request, user);

        assertThat(user.getEmail()).isEqualTo("original@example.com");  
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateUserFromDto_shouldNotUpdateRole_whenRoleIsNull() {
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("new@example.com")
                .build();  

        userMapper.updateUserFromDto(request, user);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);  
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUserFromDto_shouldDoNothing_whenRequestIsNull() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();

        String originalEmail = user.getEmail();
        UserRole originalRole = user.getRole();

        userMapper.updateUserFromDto(null, user);

        assertThat(user.getEmail()).isEqualTo(originalEmail);
        assertThat(user.getRole()).isEqualTo(originalRole);
    }
}
