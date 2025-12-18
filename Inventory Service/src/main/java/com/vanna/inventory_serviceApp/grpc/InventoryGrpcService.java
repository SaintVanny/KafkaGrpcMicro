package com.vanna.inventory_serviceApp.grpc;

import com.vanna.inventory_serviceApp.grpc.inventory.*;
import com.vanna.inventory_serviceApp.service.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

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
    
    @Override
    public void checkMultipleProductsAvailability(
            CheckMultipleProductsRequest request,
            StreamObserver<CheckMultipleProductsResponse> responseObserver
    ) {
        try {
            log.info("gRPC batch request received: itemsCount={}", request.getItemsCount());

            if (request.getItemsList() == null || request.getItemsList().isEmpty()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription("Items list cannot be empty")
                                .asRuntimeException()
                );
                return;
            }

            CheckMultipleProductsResponse.Builder responseBuilder = CheckMultipleProductsResponse.newBuilder();

            for (ProductAvailabilityItem item : request.getItemsList()) {
                String productId = item.getProductId();
                int quantity = item.getQuantity();

                log.debug("Checking product: productId={}, quantity={}", productId, quantity);

                try {
                    if (productId == null || productId.trim().isEmpty()) {
                        responseBuilder.addResults(
                                ProductAvailabilityResult.newBuilder()
                                        .setProductId(productId)
                                        .setAvailable(false)
                                        .setMessage("Product ID cannot be empty")
                                        .setActualStock(0)
                                        .setPrice(0.0)
                                        .setSale(0.0)
                                        .build()
                        );
                        continue;
                    }

                    if (quantity <= 0) {
                        responseBuilder.addResults(
                                ProductAvailabilityResult.newBuilder()
                                        .setProductId(productId)
                                        .setAvailable(false)
                                        .setMessage("Quantity must be greater than 0")
                                        .setActualStock(0)
                                        .setPrice(0.0)
                                        .setSale(0.0)
                                        .build()
                        );
                        continue;
                    }

                    CheckProductAvailabilityResponse singleCheckResponse = productService.checkAvailability(
                            productId,
                            quantity
                    );

                    responseBuilder.addResults(
                            ProductAvailabilityResult.newBuilder()
                                    .setProductId(productId)
                                    .setAvailable(singleCheckResponse.getAvailable())
                                    .setMessage(singleCheckResponse.getMessage())
                                    .setActualStock(singleCheckResponse.getActualStock())
                                    .setPrice(singleCheckResponse.getPrice())
                                    .setSale(singleCheckResponse.getSale())
                                    .build()
                    );

                } catch (Exception itemException) {
                    log.error("Error checking product: productId={}, error={}", productId, itemException.getMessage());

                    responseBuilder.addResults(
                            ProductAvailabilityResult.newBuilder()
                                    .setProductId(productId)
                                    .setAvailable(false)
                                    .setMessage("Internal error: " + itemException.getMessage())
                                    .setActualStock(0)
                                    .setPrice(0.0)
                                    .setSale(0.0)
                                    .build()
                    );
                }
            }

            CheckMultipleProductsResponse response = responseBuilder.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("gRPC batch response sent: totalItems={}, availableCount={}",
                    response.getResultsCount(),
                    response.getResultsList().stream().filter(ProductAvailabilityResult::getAvailable).count());

        } catch (Exception e) {
            log.error("Error processing gRPC batch request: error={}", e.getMessage(), e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}