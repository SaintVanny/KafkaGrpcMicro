package com.vanna.inventory_serviceApp.service;

import com.vanna.inventory_serviceApp.dto.ProductRequest;
import com.vanna.inventory_serviceApp.dto.ProductResponse;
import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;

import java.util.List;

public interface ProductService {
    
    CheckProductAvailabilityResponse checkAvailability(Long productId, Integer quantity);
    
    List<ProductResponse> getAllProducts();
    
    ProductResponse getProductById(Long id);
    
    ProductResponse createProduct(ProductRequest request);

    void deleteProduct(Long id);
}
