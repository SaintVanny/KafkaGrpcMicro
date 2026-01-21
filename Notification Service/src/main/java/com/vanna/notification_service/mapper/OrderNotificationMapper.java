package com.vanna.notification_service.mapper;

import com.vanna.notification_service.dto.notifications.OrderNotificationResponse;
import com.vanna.notification_service.entity.OrderNotification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderNotificationMapper {
    
    public OrderNotificationResponse toResponse(OrderNotification entity) {
        return OrderNotificationResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .sale(entity.getSale())
                .totalPrice(entity.getTotalPrice())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .orderName(entity.getOrderName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    public List<OrderNotificationResponse> toResponseList(List<OrderNotification> entities) {
        List<OrderNotificationResponse> responses = new ArrayList<>();

        for (OrderNotification entity : entities) {
            OrderNotificationResponse response = toResponse(entity);
            responses.add(response);
        }

        return responses;
    }
}