package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface OrderQueryService {
    OrderDto getOrderById(UUID orderId, Authentication authentication);

    Page<OrderDto> getOrdersByCustomerId(UUID customerId, Pageable pageable);
}
