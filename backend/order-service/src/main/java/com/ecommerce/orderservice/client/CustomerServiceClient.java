package com.ecommerce.orderservice.client;

import java.util.UUID;

public interface CustomerServiceClient {
    boolean validateCustomer(UUID customerId);
}
