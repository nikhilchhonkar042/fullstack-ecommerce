package com.ecommerce.orderservice.client;

import java.util.List;

public interface InventoryServiceClient {
    boolean checkAvailability(List<?> items);
}
