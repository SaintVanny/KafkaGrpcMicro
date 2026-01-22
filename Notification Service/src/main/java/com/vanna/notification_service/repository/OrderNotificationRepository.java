package com.vanna.notification_service.repository;

import com.vanna.notification_service.entity.OrderNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderNotificationRepository extends JpaRepository<OrderNotification, UUID> {
    
    List<OrderNotification> findByOrderId(UUID orderId);
    
    List<OrderNotification> findByUserId(UUID userId);
    
    boolean existsByOrderId(UUID orderId);
}