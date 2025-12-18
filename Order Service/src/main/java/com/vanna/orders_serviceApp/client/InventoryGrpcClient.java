package com.vanna.orders_serviceApp.client;

import com.vanna.orders_serviceApp.grpc.inventory.CheckProductAvailabilityRequest;
import com.vanna.orders_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;
import com.vanna.orders_serviceApp.grpc.inventory.InventoryServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
//todo check please
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
}