package com.vanna.inventory_serviceApp.service;

import com.vanna.inventory_serviceApp.dto.ProductRequest;
import com.vanna.inventory_serviceApp.dto.ProductResponse;
import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    CheckProductAvailabilityResponse checkAvailability(String productId, Integer quantity);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse createProduct(ProductRequest request);

    void deleteProduct(UUID id);
}
