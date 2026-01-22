package com.vanna.notification_service.service.impl;

import com.vanna.notification_service.dto.notifications.OrderNotificationResponse;
import com.vanna.notification_service.entity.OrderNotification;
import com.vanna.notification_service.mapper.OrderNotificationMapper;
import com.vanna.notification_service.repository.OrderNotificationRepository;
import com.vanna.notification_service.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  
public class OrderNotificationServiceImpl implements OrderNotificationService {

    private final OrderNotificationRepository repository;
    private final OrderNotificationMapper mapper;

    @Override
    public List<OrderNotificationResponse> getAllOrders() {
        log.debug("Get all orders from db");

        List<OrderNotification> entities = repository.findAll();

        log.info("Get {} position of orders in system", entities.size());

        return mapper.toResponseList(entities);
    }

    @Override
    public List<OrderNotificationResponse> getOrdersByOrderId(UUID orderId) {
        log.debug("Get orders by orderId: {}", orderId);
        
        List<OrderNotification> entities = repository.findByOrderId(orderId);

        log.info("Get {} positions for orderId: {}", entities.size(), orderId);

        return mapper.toResponseList(entities);
    }

    @Override
    public List<OrderNotificationResponse> getOrdersByUserId(UUID userId) {
        log.debug("Get user's orders by userId: {}", userId);

        List<OrderNotification> entities = repository.findByUserId(userId);

        log.info("Get {} positions for userId: {}", entities.size(), userId);

        return mapper.toResponseList(entities);
    }
}