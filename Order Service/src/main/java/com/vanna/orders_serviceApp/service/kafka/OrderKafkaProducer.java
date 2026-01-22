package com.vanna.orders_serviceApp.service.kafka;

import com.vanna.orders_serviceApp.dto.kafka.OrderEvent;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.grpc.inventory.ProductAvailabilityResult;
import com.vanna.orders_serviceApp.mapper.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderEventMapper orderEventMapper;

    @Value("${kafka.topics.orders:orders}")
    private String ordersTopic;
    
    public void sendOrderEvent(Order order, Map<UUID, ProductAvailabilityResult> inventoryData) {
        try {
            log.debug("Prepare event for kafka: orderId={}", order.getId());

            OrderEvent event = orderEventMapper.toEvent(order, inventoryData);

            String key = order.getId().toString();
            CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(ordersTopic, key, event);

            SendResult<String, OrderEvent> result = future.get(5, TimeUnit.SECONDS);

            log.info("Event was send to Kafka: orderId={}, topic={}, partition={}, offset={}",
                    order.getId(),
                    ordersTopic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

        } catch (Exception e) {
            log.error("Dont sent event to kafka( orderId={}, error={}",
                    order.getId(), e.getMessage(), e);

            throw new RestOrdersException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to publish order event to Kafka: " + e.getMessage()
            );
        }
    }
}