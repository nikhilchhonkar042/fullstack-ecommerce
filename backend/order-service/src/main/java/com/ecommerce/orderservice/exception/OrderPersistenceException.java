package com.ecommerce.orderservice.exception;

public class OrderPersistenceException extends RuntimeException {
    public OrderPersistenceException(String message) {
        super(message);
    }

    public OrderPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
