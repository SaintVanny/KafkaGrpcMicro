package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void toResponse_shouldMapAllFieldsIncludingNestedUser() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .role(UserRole.USER)
                .build();

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setDescription("Test order description");
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(createdAt);

        // When
        OrderResponse response = orderMapper.toResponse(order);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getDescription()).isEqualTo("Test order description");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toResponse_shouldMapDifferentOrderStatuses() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .role(UserRole.USER)
                .build();

        Order inProgressOrder = new Order();
        inProgressOrder.setId(UUID.randomUUID());
        inProgressOrder.setUser(user);
        inProgressOrder.setDescription("In progress order");
        inProgressOrder.setStatus(OrderStatus.IN_PROGRESS);
        inProgressOrder.setCreatedAt(LocalDateTime.now());

        // When
        OrderResponse response = orderMapper.toResponse(inProgressOrder);

        // Then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    void toResponse_shouldReturnNull_whenOrderIsNull() {
        // When
        OrderResponse response = orderMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void toResponseList_shouldMapAllOrders() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .role(UserRole.USER)
                .build();

        Order order1 = new Order();
        order1.setId(UUID.randomUUID());
        order1.setUser(user);
        order1.setDescription("Order 1");
        order1.setStatus(OrderStatus.CREATED);
        order1.setCreatedAt(LocalDateTime.now());

        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setUser(user);
        order2.setDescription("Order 2");
        order2.setStatus(OrderStatus.IN_PROGRESS);
        order2.setCreatedAt(LocalDateTime.now());

        List<Order> orders = List.of(order1, order2);

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getDescription()).isEqualTo("Order 1");
        assertThat(responses.get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(responses.get(1).getDescription()).isEqualTo("Order 2");
        assertThat(responses.get(1).getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    void toResponseList_shouldReturnEmptyList_whenInputIsEmpty() {
        // Given
        List<Order> emptyList = List.of();

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(emptyList);

        // Then
        assertThat(responses).isEmpty();
    }

    @Test
    void toResponseList_shouldReturnNull_whenListIsNull() {
        // When
        List<OrderResponse> responses = orderMapper.toResponseList(null);

        // Then
        assertThat(responses).isNull();
    }
}
