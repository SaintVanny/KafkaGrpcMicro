package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.entity.OrderItem;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void toResponse_shouldMapAllFieldsIncludingNestedUserAndItems() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .role(UserRole.USER)
                .build();

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setOrderName("Test order with multiple items");
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(createdAt);

        OrderItem item1 = new OrderItem();
        item1.setId(UUID.randomUUID());
        item1.setOrder(order);
        item1.setProductId(productId1);
        item1.setQuantity(5);

        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());
        item2.setOrder(order);
        item2.setProductId(productId2);
        item2.setQuantity(3);

        order.setItems(List.of(item1, item2));

        // When
        OrderResponse response = orderMapper.toResponse(order);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getOrderName()).isEqualTo("Test order with multiple items");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);

        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getProductId()).isEqualTo(productId1);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(5);
        assertThat(response.getItems().get(1).getProductId()).isEqualTo(productId2);
        assertThat(response.getItems().get(1).getQuantity()).isEqualTo(3);
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
        inProgressOrder.setOrderName("In progress order");
        inProgressOrder.setStatus(OrderStatus.IN_PROGRESS);
        inProgressOrder.setCreatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setOrder(inProgressOrder);
        item.setProductId(UUID.randomUUID());
        item.setQuantity(3);
        inProgressOrder.setItems(List.of(item));

        // When
        OrderResponse response = orderMapper.toResponse(inProgressOrder);

        // Then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(response.getItems()).hasSize(1);
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
        order1.setOrderName("Order 1");
        order1.setStatus(OrderStatus.CREATED);
        order1.setCreatedAt(LocalDateTime.now());

        OrderItem item1 = new OrderItem();
        item1.setId(UUID.randomUUID());
        item1.setOrder(order1);
        item1.setProductId(UUID.randomUUID());
        item1.setQuantity(2);
        order1.setItems(List.of(item1));

        Order order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setUser(user);
        order2.setOrderName("Order 2");
        order2.setStatus(OrderStatus.IN_PROGRESS);
        order2.setCreatedAt(LocalDateTime.now());

        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());
        item2.setOrder(order2);
        item2.setProductId(UUID.randomUUID());
        item2.setQuantity(7);
        order2.setItems(List.of(item2));

        List<Order> orders = List.of(order1, order2);

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getOrderName()).isEqualTo("Order 1");
        assertThat(responses.get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(responses.get(0).getItems()).hasSize(1);
        assertThat(responses.get(1).getOrderName()).isEqualTo("Order 2");
        assertThat(responses.get(1).getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(responses.get(1).getItems()).hasSize(1);
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
