package com.ecommerce.orderservice.client;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerServiceClientImpl implements CustomerServiceClient {
    @Override
    public boolean validateCustomer(UUID customerId) {
        // Placeholder - integrate with actual customer service
        return true;
    }
}
