package com.ecommerce.orderservice.support;

import org.springframework.util.concurrent.ListenableFuture;

public interface LoadBalancedKafkaTemplate {
    <K, V> ListenableFuture<?> send(String topic, K key, V data);
}
