package com.vanna.orders_serviceApp.service.impl;

import com.vanna.orders_serviceApp.config.correlationId.CorrelationIdProvider;
import com.vanna.orders_serviceApp.dto.orders.CreateOrderRequest;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.dto.orders.UpdateOrderStatusRequest;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.mapper.OrderMapper;
import com.vanna.orders_serviceApp.repository.OrderRepository;
import com.vanna.orders_serviceApp.service.OrderService;
import com.vanna.orders_serviceApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CorrelationIdProvider correlationIdProvider;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        UUID orderId = correlationIdProvider.getCurrentCorrelationId();
        log.debug("Creating order: orderId={}, description={}", orderId, request.getDescription());

        User currentUser = userService.getCurrentAuthenticatedUser();
        log.debug("Order will be created for user: userId={}, username={}",
                currentUser.getId(), currentUser.getUsername());

        Order order = new Order();
        order.setId(orderId);
        order.setUser(currentUser);
        order.setDescription(request.getDescription());
        order.setStatus(OrderStatus.CREATED);

        try {
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully: orderId={}, userId={}, status={}",
                    savedOrder.getId(), savedOrder.getUser().getId(), savedOrder.getStatus());
            return orderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to create order: orderId={}, userId={}, error={}",
                    orderId, currentUser.getId(), e.getMessage(), e);
            throw new RestOrdersException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create order: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCurrentUserOrders() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        log.debug("Fetching orders for user: userId={}", currentUser.getId());

        List<Order> orders = orderRepository.findByUserId(currentUser.getId());
        log.info("Found {} orders for user: userId={}", orders.size(), currentUser.getId());

        return orderMapper.toResponseList(orders);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders (admin operation)");

        List<Order> orders = orderRepository.findAll();
        log.info("Found {} orders in system", orders.size());

        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        log.debug("Updating order status: orderId={}, newStatus={}", orderId, request.getStatus());

        Order order = findOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        try {
            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated: orderId={}, oldStatus={}, newStatus={}",
                    orderId, oldStatus, updatedOrder.getStatus());
            return orderMapper.toResponse(updatedOrder);
        } catch (Exception e) {
            log.error("Failed to update order status: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw new RestOrdersException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update order status");
        }
    }

    @Override
    @Transactional
    public String deleteOrder(UUID orderId) {
        log.debug("Attempting to delete order: orderId={}", orderId);

        Order order = findOrderById(orderId);
        User currentUser = userService.getCurrentAuthenticatedUser();

        if (!isOrderOwner(order, currentUser) && !isAdmin(currentUser)) {
            log.warn("Unauthorized delete attempt: orderId={}, userId={}, userRole={}",
                    orderId, currentUser.getId(), currentUser.getRole());
            throw new RestOrdersException(HttpStatus.FORBIDDEN, "Access denied: You can only delete your own orders");
        }

        String orderDescription = order.getDescription();
        orderRepository.delete(order);
        log.info("Order deleted successfully: orderId={}, deletedBy={}",
                orderId, currentUser.getUsername());

        return "Order '" + orderDescription + "' (ID: " + orderId + ") has been successfully deleted";
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId={}", orderId);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId);
                });
    }
    
    private boolean isOrderOwner(Order order, User user) {
        return order.getUser().getId().equals(user.getId());
    }
    
    private boolean isAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }
}
