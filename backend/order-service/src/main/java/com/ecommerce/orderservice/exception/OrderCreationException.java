package com.ecommerce.orderservice.exception;

public class OrderCreationException extends RuntimeException {
    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable t) {
        super(message, t);
    }
}
