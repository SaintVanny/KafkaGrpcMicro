package com.vanna.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    @Column(name = "order_id", nullable = false)
    private UUID orderId;  

    @Column(name = "product_id", nullable = false)
    private UUID productId;  

    @Column(nullable = false)
    private Integer quantity;  

    @Column(nullable = false)
    private Double price;  

    @Column(nullable = false)
    private Double sale;  

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;  

    @Column(name = "user_id", nullable = false)
    private UUID userId;  

    @Column(length = 100)
    private String username;  

    @Column(name = "order_name", length = 255)
    private String orderName;  

    @Column(length = 50)
    private String status;  

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  
}