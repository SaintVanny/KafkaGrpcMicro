package com.vanna.inventory_serviceApp.mapper;

import com.vanna.inventory_serviceApp.dto.ProductRequest;
import com.vanna.inventory_serviceApp.dto.ProductResponse;
import com.vanna.inventory_serviceApp.entity.Product;
import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .sale(product.getSale())
                .build();
    }
    
    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }
        return Product.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .sale(request.getSale())
                .build();
    }
    
    public CheckProductAvailabilityResponse toGrpcResponse(
            Product product,
            boolean available,
            String message
    ) {
        if (product == null) {
            return CheckProductAvailabilityResponse.newBuilder()
                    .setAvailable(false)
                    .setMessage("Product not found")
                    .setActualStock(0)
                    .setPrice(0.0)
                    .setSale(0.0)
                    .build();
        }
        
        return CheckProductAvailabilityResponse.newBuilder()
                .setAvailable(available)
                .setMessage(message)
                .setActualStock(product.getQuantity())
                .setPrice(product.getPrice())
                .setSale(product.getSale())
                .build();
    }
}
