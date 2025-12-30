package com.ecommerce.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemDto {
    private UUID productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
