package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.kafka.OrderEvent;
import com.vanna.orders_serviceApp.dto.kafka.OrderItemEvent;
import com.vanna.orders_serviceApp.entity.Order;
import com.vanna.orders_serviceApp.entity.OrderItem;
import com.vanna.orders_serviceApp.grpc.inventory.ProductAvailabilityResult;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderEventMapper {
    
    public OrderEvent toEvent(Order order, Map<UUID, ProductAvailabilityResult> inventoryData) {
        List<OrderItemEvent> itemEvents = convertItems(order.getItems(), inventoryData);

        Double totalPrice = calculateTotal(itemEvents);

        return OrderEvent.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .orderName(order.getOrderName())
                .items(itemEvents)
                .totalPrice(totalPrice)
                .status(order.getStatus().toString())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private List<OrderItemEvent> convertItems(List<OrderItem> items, Map<UUID, ProductAvailabilityResult> inventoryData) {
        List<OrderItemEvent> result = new ArrayList<>();

        for (OrderItem item : items) {
            UUID productId = item.getProductId();

            ProductAvailabilityResult inventoryResult = inventoryData.get(productId);

            if (inventoryResult != null) {
                OrderItemEvent itemEvent = convertOneItem(item, inventoryResult);
                result.add(itemEvent);
            }
        }

        return result;
    }

    private OrderItemEvent convertOneItem(OrderItem item, ProductAvailabilityResult inventoryResult) {
        int quantity = item.getQuantity();
        double price = inventoryResult.getPrice();
        double sale = inventoryResult.getSale();

        double itemTotal = (price * quantity) * (1 - sale / 100.0);

        return OrderItemEvent.builder()
                .productId(item.getProductId())
                .quantity(quantity)
                .price(price)
                .sale(sale)
                .itemTotalPrice(itemTotal)
                .build();
    }

    private Double calculateTotal(List<OrderItemEvent> items) {
        double total = 0.0;

        for (OrderItemEvent item : items) {
            total += item.getItemTotalPrice();
        }

        return total;
    }
}