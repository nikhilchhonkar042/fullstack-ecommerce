package com.ecommerce.orderservice.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
}
