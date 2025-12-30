package com.ecommerce.orderservice.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private String shippingAddress;
    private String paymentMethodId;
    private String idempotencyKey;
}
