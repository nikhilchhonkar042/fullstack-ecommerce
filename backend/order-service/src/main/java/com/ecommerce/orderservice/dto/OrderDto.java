package com.ecommerce.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID id;
    private UUID customerId;
    private List<OrderItemDto> items;
    private String status;
    private BigDecimal totalAmount;
    private Instant createdAt;
}
