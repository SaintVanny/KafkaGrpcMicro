package com.vanna.orders_serviceApp.mapper;

import com.vanna.orders_serviceApp.dto.orders.OrderItemResponse;
import com.vanna.orders_serviceApp.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse toResponse(OrderItem orderItem);
    List<OrderItemResponse> toResponseList(List<OrderItem> orderItems);
}