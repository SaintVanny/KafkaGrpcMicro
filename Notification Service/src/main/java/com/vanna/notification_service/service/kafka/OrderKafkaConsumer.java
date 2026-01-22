package com.vanna.notification_service.service.kafka;

import com.vanna.notification_service.dto.kafka.OrderEvent;
import com.vanna.notification_service.dto.kafka.OrderItemEvent;
import com.vanna.notification_service.entity.OrderNotification;
import com.vanna.notification_service.repository.OrderNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderNotificationRepository repository;
    
    @KafkaListener(
        topics = "${kafka.topics.orders}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consumeOrderEvent(OrderEvent event) {
        try {
            log.info("Get event from Kafka: orderId={}, items={}, totalPrice={}",
                    event.getOrderId(), event.getItems().size(), event.getTotalPrice());
            
            if (repository.existsByOrderId(event.getOrderId())) {
                log.warn("Order exists, skip: orderId={}", event.getOrderId());
                return;
            }
            
            LocalDateTime createdAt = event.getCreatedAt();
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
                log.warn("createdAt is null in Kafka event, using fallback: orderId={}, fallbackTime={}",
                        event.getOrderId(), createdAt);
            } //todo remove it when fix null created_at in orderService

            List<OrderNotification> notifications = new ArrayList<>();

            for (OrderItemEvent item : event.getItems()) {
                OrderNotification notification = new OrderNotification();

                notification.setOrderId(event.getOrderId());
                notification.setUserId(event.getUserId());
                notification.setUsername(event.getUsername());
                notification.setOrderName(event.getOrderName());
                notification.setStatus(event.getStatus());
                notification.setCreatedAt(createdAt);

                notification.setProductId(item.getProductId());
                notification.setQuantity(item.getQuantity());
                notification.setPrice(item.getPrice());
                notification.setSale(item.getSale());
                notification.setTotalPrice(item.getItemTotalPrice());

                notifications.add(notification);
            }
            
            repository.saveAll(notifications);

            log.info("Saved {} position for order: orderId={}, userId={}",
                    notifications.size(), event.getOrderId(), event.getUserId());

        } catch (Exception e) {
            log.error("Exception in event: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }
}