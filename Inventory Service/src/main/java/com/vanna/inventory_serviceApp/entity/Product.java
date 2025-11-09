package com.vanna.inventory_serviceApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//todo
    private Long id;

    @NotNull(message = "Product name cannot be null")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be >= 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be >= 0")
    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double sale = 0.0;  // default 0.0% 
}