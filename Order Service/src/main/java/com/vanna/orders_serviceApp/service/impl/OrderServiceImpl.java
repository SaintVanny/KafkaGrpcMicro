package com.vanna.orders_serviceApp.service.impl;

import com.vanna.orders_serviceApp.client.InventoryGrpcClient;
import com.vanna.orders_serviceApp.config.correlationId.CorrelationIdProvider;
import com.vanna.orders_serviceApp.dto.orders.CreateOrderRequest;
import com.vanna.orders_serviceApp.dto.orders.OrderItemRequest;
import com.vanna.orders_serviceApp.dto.orders.OrderResponse;
import com.vanna.orders_serviceApp.dto.orders.UpdateOrderStatusRequest;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.entity.OrderItem;
import com.vanna.orders_serviceApp.entity.User;
import com.vanna.orders_serviceApp.entity.enm.OrderStatus;
import com.vanna.orders_serviceApp.entity.enm.UserRole;
import com.vanna.orders_serviceApp.exception.RestOrdersException;
import com.vanna.orders_serviceApp.grpc.inventory.CheckProductAvailabilityResponse;
import com.vanna.orders_serviceApp.grpc.inventory.ProductAvailabilityResult;
import com.vanna.orders_serviceApp.mapper.OrderMapper;
import com.vanna.orders_serviceApp.repository.OrderRepository;
import com.vanna.orders_serviceApp.service.OrderService;
import com.vanna.orders_serviceApp.service.UserService;
import com.vanna.orders_serviceApp.service.kafka.OrderKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CorrelationIdProvider correlationIdProvider;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final OrderKafkaProducer orderKafkaProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        UUID orderId = correlationIdProvider.getCurrentCorrelationId();
        log.debug("Creating order: orderId={}, itemsCount={}, orderName={}",
                orderId, request.getItems().size(), request.getOrderName());

        User currentUser = userService.getCurrentAuthenticatedUser();
        log.debug("Order will be created for user: userId={}, username={}",
                currentUser.getId(), currentUser.getUsername());

        Map<UUID, Integer> productQuantities = sumDuplicateProducts(request.getItems());
        log.debug("After merging duplicates: uniqueProducts={}", productQuantities.size());

        Map<UUID, ProductAvailabilityResult> availabilityResults =
                inventoryGrpcClient.checkMultipleProductsAvailability(productQuantities);

        Map<UUID, Integer> availableProducts = new HashMap<>();
        List<String> warnings = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();
            ProductAvailabilityResult result = availabilityResults.get(productId);

            if (result != null && result.getAvailable()) {
                availableProducts.put(productId, quantity);
                log.debug("Product available: productId={}, quantity={}", productId, quantity);
            } else {
                String warningMessage = result != null
                        ? String.format("Product %s: %s (requested: %d, available: %d)",
                        productId, result.getMessage(), quantity, result.getActualStock())
                        : String.format("Product %s: Unavailable (requested: %d)", productId, quantity);
                warnings.add(warningMessage);
                log.warn("Product unavailable: {}", warningMessage);
            }
        }

        if (availableProducts.isEmpty()) {
            log.error("Order rejected: all products unavailable. OrderId={}, totalProducts={}, unavailableProducts={}",
                    orderId, productQuantities.size(), warnings.size());
            throw new RestOrdersException(HttpStatus.BAD_REQUEST,
                    "Cannot create order: all requested products are unavailable. Details: " + String.join("; ", warnings));
        }

        Order order = new Order();
        order.setId(orderId);
        order.setUser(currentUser);
        order.setOrderName(request.getOrderName());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : availableProducts.entrySet()) {
            OrderItem item = new OrderItem();
            item.setId(UUID.randomUUID());
            item.setOrder(order);
            item.setProductId(entry.getKey());
            item.setQuantity(entry.getValue());
            orderItems.add(item);
        }
        order.setItems(orderItems);

        try {
            Order savedOrder = orderRepository.save(order);
            log.info("Order saved to database: orderId={}, userId={}, totalItemsRequested={}, itemsCreated={}, warnings={}",
                    savedOrder.getId(), savedOrder.getUser().getId(),
                    productQuantities.size(), savedOrder.getItems().size(), warnings.size());

            try {
                orderKafkaProducer.sendOrderEvent(savedOrder, availabilityResults);
            } catch (RestOrdersException kafkaException) {
                log.error("Order creation rolled back due to Kafka failure: orderId={}", savedOrder.getId());
                throw kafkaException;
            }

            OrderResponse response = orderMapper.toResponse(savedOrder);
            response.setWarnings(warnings);

            return response;
        } catch (Exception e) {
            log.error("Failed to create order: orderId={}, userId={}, error={}",
                    orderId, currentUser.getId(), e.getMessage(), e);
            throw new RestOrdersException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create order: " + e.getMessage());
        }
    }
    
    private Map<UUID, Integer> sumDuplicateProducts(List<OrderItemRequest> items) {
        Map<UUID, Integer> productQuantities = new HashMap<>();
        for (OrderItemRequest item : items) {
            productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return productQuantities;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCurrentUserOrders() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        log.debug("Fetching orders for user: userId={}", currentUser.getId());

        List<Order> orders = orderRepository.findByUserId(currentUser.getId());
        log.info("Found {} orders for user: userId={}", orders.size(), currentUser.getId());

        return orderMapper.toResponseList(orders);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders (admin operation)");

        List<Order> orders = orderRepository.findAll();
        log.info("Found {} orders in system", orders.size());

        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        log.debug("Updating order status: orderId={}, newStatus={}", orderId, request.getStatus());

        Order order = findOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        try {
            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated: orderId={}, oldStatus={}, newStatus={}",
                    orderId, oldStatus, updatedOrder.getStatus());
            return orderMapper.toResponse(updatedOrder);
        } catch (Exception e) {
            log.error("Failed to update order status: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw new RestOrdersException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update order status");
        }
    }

    @Override
    @Transactional
    public String deleteOrder(UUID orderId) {
        log.debug("Attempting to delete order: orderId={}", orderId);

        Order order = findOrderById(orderId);
        User currentUser = userService.getCurrentAuthenticatedUser();

        if (!isOrderOwner(order, currentUser) && !isAdmin(currentUser)) {
            log.warn("Unauthorized delete attempt: orderId={}, userId={}, userRole={}",
                    orderId, currentUser.getId(), currentUser.getRole());
            throw new RestOrdersException(HttpStatus.FORBIDDEN, "Access denied: You can only delete your own orders");
        }

        String orderNameForResponse = order.getOrderName();
        orderRepository.delete(order);
        log.info("Order deleted successfully: orderId={}, deletedBy={}",
                orderId, currentUser.getUsername());

        return "Order '" + orderNameForResponse + "' (ID: " + orderId + ") has been successfully deleted";
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId={}", orderId);
                    return new RestOrdersException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId);
                });
    }
    
    private boolean isOrderOwner(Order order, User user) {
        return order.getUser().getId().equals(user.getId());
    }
    
    private boolean isAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }
    
    private void checkProductAvailability(UUID productId, Integer quantity) {
        log.debug("Checking inventory for product: productId={}, quantity={}", productId, quantity);

        CheckProductAvailabilityResponse response =
                inventoryGrpcClient.checkProductAvailability(productId, quantity);

        if (!response.getAvailable()) {
            log.warn("Product unavailable: productId={}, requestedQuantity={}, actualStock={}",
                    productId, quantity, response.getActualStock());

            throw new RestOrdersException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Product is not available. Requested: %d, Available: %d. %s",
                            quantity, response.getActualStock(), response.getMessage())
            );
        }

        log.info("Product availability confirmed: productId={}, quantity={}", productId, quantity);
    }
}
