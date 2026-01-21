package com.vanna.notification_service.service;

import com.vanna.notification_service.dto.notifications.OrderNotificationResponse;

import java.util.List;
import java.util.UUID;

public interface OrderNotificationService {
    
    List<OrderNotificationResponse> getAllOrders();
    
    List<OrderNotificationResponse> getOrdersByOrderId(UUID orderId);
    
    List<OrderNotificationResponse> getOrdersByUserId(UUID userId);
}