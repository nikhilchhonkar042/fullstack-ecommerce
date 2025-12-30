package com.ecommerce.orderservice.client;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentServiceClientImpl implements PaymentServiceClient {
    @Override
    public boolean validatePaymentMethod(UUID customerId, String paymentMethodId) {
        // Placeholder - integrate with actual payment service
        return true;
    }
}
