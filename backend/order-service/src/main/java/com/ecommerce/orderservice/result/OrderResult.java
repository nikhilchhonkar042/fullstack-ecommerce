package com.ecommerce.orderservice.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResult<T> {
    private boolean success;
    private T order;
    private String errorMessage;

    public static <T> OrderResult<T> success(T order) {
        return new OrderResult<>(true, order, null);
    }

    public static <T> OrderResult<T> failure(String message) {
        return new OrderResult<>(false, null, message);
    }
}
