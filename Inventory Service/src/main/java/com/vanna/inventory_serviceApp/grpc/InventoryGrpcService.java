package com.vanna.inventory_serviceApp.grpc;

import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityRequest;
import com.vanna.inventory_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;
import com.vanna.inventory_serviceApp.grpc.inventory.InventoryServiceGrpc;
import com.vanna.inventory_serviceApp.service.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
//todo check please
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ProductService productService;


    @Override
    public void checkProductAvailability(
            CheckProductAvailabilityRequest request,
            StreamObserver<CheckProductAvailabilityResponse> responseObserver
    ) {
        try {
            log.info("gRPC request received: productId={}, quantity={}",
                    request.getProductId(), request.getQuantity());

            if (request.getProductId() == null || request.getProductId().trim().isEmpty()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Product ID cannot be empty")
                                .asRuntimeException()
                );
                return;
            }

            if (request.getQuantity() <= 0) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Quantity must be greater than 0")
                                .asRuntimeException()
                );
                return;
            }

            CheckProductAvailabilityResponse response = productService.checkAvailability(
                    request.getProductId(),
                    request.getQuantity()
            );

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("gRPC response sent: available={}, actualStock={}, price={}, sale={}",
                    response.getAvailable(), response.getActualStock(),
                    response.getPrice(), response.getSale());

        } catch (Exception e) {
            log.error("Error processing gRPC request: productId={}, quantity={}, error={}",
                    request.getProductId(), request.getQuantity(), e.getMessage(), e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}