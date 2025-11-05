package com.vanna.orders_serviceApp.service;

import com.vanna.orders_serviceApp.dto.orders.CreateOrderRequest;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.dto.orders.UpdateOrderStatusRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getCurrentUserOrders();

    List<OrderResponse> getAllOrders();

    OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request);

    String deleteOrder(UUID orderId);
}
