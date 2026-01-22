package com.vanna.notification_service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private UUID productId;
    private Integer quantity;
    private Double price;
    private Double sale;
    private Double itemTotalPrice;
}