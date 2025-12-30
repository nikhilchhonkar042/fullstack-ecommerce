package com.ecommerce.orderservice.client;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryServiceClientImpl implements InventoryServiceClient {
    @Override
    public boolean checkAvailability(List<?> items) {
        // Placeholder - integrate with actual inventory service
        return true;
    }
}
