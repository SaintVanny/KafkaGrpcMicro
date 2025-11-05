package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.users.UserResponse;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для UserMapper
 * Проверяют корректность маппинга User entity → UserResponse DTO
 */
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
                .role(UserRole.USER)
                .build();

        // When
        UserResponse response = userMapper.toResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
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

        // When
        UserResponse response = userMapper.toResponse(admin);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void toResponse_shouldReturnNull_whenUserIsNull() {
        // When
        UserResponse response = userMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }
}
