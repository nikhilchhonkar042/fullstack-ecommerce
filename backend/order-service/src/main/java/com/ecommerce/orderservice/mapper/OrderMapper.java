package com.ecommerce.orderservice.mapper;

import com.ecommerce.orderservice.dto.OrderDto;
import com.ecommerce.orderservice.result.OrderResult;

public final class OrderMapper {
    public static OrderDto toDto(Object order) {
        // Minimal placeholder mapping
        return OrderDto.builder().build();
    }

    public static OrderResult toResult(Object order) {
        return OrderResult.success(order);
    }
}
