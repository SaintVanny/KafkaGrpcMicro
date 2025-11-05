package com.vanna.orders_serviceApp.mapper;

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
        // Given
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
        // Given
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
}
