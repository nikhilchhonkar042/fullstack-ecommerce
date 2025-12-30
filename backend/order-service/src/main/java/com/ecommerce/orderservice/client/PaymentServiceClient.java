package com.ecommerce.orderservice.client;

import java.util.UUID;

public interface PaymentServiceClient {
    boolean validatePaymentMethod(UUID customerId, String paymentMethodId);
}
