package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.command.CreateOrderCommand;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.request.OrderItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFactory {

    private final Clock clock;

    public Order createOptimizedOrder(CreateOrderCommand command) {
        List<OrderItem> orderItems = ((List<?>) command.getItems()).stream()
                .map(item -> (OrderItemRequest) item)
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        return Order.builder()
                .id(UUID.randomUUID())
                .customerId(command.getCustomerId())
                .items(orderItems)
                .status("PENDING")
                .totalAmount(totalAmount)
                .shippingAddress(command.getShippingAddress())
                .createdAt(clock.instant())
                .version(0L)
                .build();
    }

    private OrderItem createOrderItem(OrderItemRequest itemRequest) {
        return OrderItem.builder()
                .productId(UUID.fromString(itemRequest.getProductId()))
                .quantity(itemRequest.getQuantity())
                .unitPrice(itemRequest.getUnitPrice())
                .totalPrice(itemRequest.getUnitPrice().multiply(
                        BigDecimal.valueOf(itemRequest.getQuantity())))
                .build();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
