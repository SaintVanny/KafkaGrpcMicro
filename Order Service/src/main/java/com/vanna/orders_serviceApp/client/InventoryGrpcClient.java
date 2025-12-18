package com.vanna.orders_serviceApp.client;

import com.vanna.orders_serviceApp.grpc.inventory.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class InventoryGrpcClient {

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceStub;

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackCheckAvailability")
    public CheckProductAvailabilityResponse checkProductAvailability(UUID productId, Integer quantity) {
        log.debug("check product availability by jRpc: productId={}, quantity={}", productId, quantity);

        try {
            CheckProductAvailabilityRequest request = CheckProductAvailabilityRequest.newBuilder()
                    .setProductId(productId.toString())
                    .setQuantity(quantity)
                    .build();

            CheckProductAvailabilityResponse response = inventoryServiceStub.checkProductAvailability(request);

            log.info("Availability product checked: productId={}, quantity={}, available={}, actualStock={}, message={}",
                    productId, quantity, response.getAvailable(), response.getActualStock(), response.getMessage());

            return response;

        } catch (StatusRuntimeException e) {
            log.error("gRPC request Inventory Service unsuccess: productId={}, quantity={}, status={}, description={}",
                    productId, quantity, e.getStatus().getCode(), e.getStatus().getDescription(), e);
            throw e; 
        }
    }
    
    private CheckProductAvailabilityResponse fallbackCheckAvailability(UUID productId, Integer quantity, Throwable throwable) {
        log.warn("Circuit Breaker OPENED: Inventory Service unavailable. Return  fallback answer. " +
                        "productId={}, quantity={}, error={}",
                productId, quantity, throwable.getMessage());

        return CheckProductAvailabilityResponse.newBuilder()
                .setAvailable(false)
                .setMessage("Inventory Service isn't in work now. Try latter.")
                .setActualStock(0)
                .build();
    }
    
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackCheckMultipleProducts")
    public Map<UUID, ProductAvailabilityResult> checkMultipleProductsAvailability(Map<UUID, Integer> productQuantities) {
        log.debug("Batch check products availability via gRPC: productsCount={}", productQuantities.size());

        try {
            CheckMultipleProductsRequest.Builder requestBuilder = CheckMultipleProductsRequest.newBuilder();

            for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
                ProductAvailabilityItem item = ProductAvailabilityItem.newBuilder()
                        .setProductId(entry.getKey().toString())
                        .setQuantity(entry.getValue())
                        .build();
                requestBuilder.addItems(item);
            }

            CheckMultipleProductsRequest request = requestBuilder.build();

            CheckMultipleProductsResponse response = inventoryServiceStub.checkMultipleProductsAvailability(request);

            Map<UUID, ProductAvailabilityResult> results = new HashMap<>();
            for (ProductAvailabilityResult result : response.getResultsList()) {
                UUID productId = UUID.fromString(result.getProductId());
                results.put(productId, result);
            }

            long availableCount = results.values().stream().filter(ProductAvailabilityResult::getAvailable).count();
            log.info("Batch availability check completed: totalProducts={}, availableProducts={}, unavailableProducts={}",
                    results.size(), availableCount, results.size() - availableCount);

            return results;

        } catch (StatusRuntimeException e) {
            log.error("gRPC batch request to Inventory Service failed: productsCount={}, status={}, description={}",
                    productQuantities.size(), e.getStatus().getCode(), e.getStatus().getDescription(), e);
            throw e;
        }
    }
    
    private Map<UUID, ProductAvailabilityResult> fallbackCheckMultipleProducts(
            Map<UUID, Integer> productQuantities,
            Throwable throwable) {

        log.warn("Circuit Breaker OPENED for batch check: Inventory Service unavailable. " +
                        "Returning fallback for {} products. Error: {}",
                productQuantities.size(), throwable.getMessage());

        Map<UUID, ProductAvailabilityResult> fallbackResults = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            ProductAvailabilityResult result = ProductAvailabilityResult.newBuilder()
                    .setProductId(entry.getKey().toString())
                    .setAvailable(false)
                    .setMessage("Inventory Service is currently unavailable. Please try again later.")
                    .setActualStock(0)
                    .setPrice(0.0)
                    .setSale(0.0)
                    .build();
            fallbackResults.put(entry.getKey(), result);
        }
        return fallbackResults;
    }
}