package com.vanna.notification_service.dto.notifications;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationResponse {

    private UUID id;  
    private UUID orderId;  
    private UUID productId;  
    private Integer quantity;  
    private Double price;  
    private Double sale;  
    private Double totalPrice;  
    private UUID userId;  
    private String username;  
    private String orderName;  
    private String status;  

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;  
}