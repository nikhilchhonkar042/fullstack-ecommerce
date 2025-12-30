package com.ecommerce.orderservice.command;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateOrderCommand {
    private UUID customerId;
    private List<?> items;
    private String shippingAddress;
    private String paymentMethodId;
    private String idempotencyKey;
}
