package com.ecommerce.orderservice.support;

import org.springframework.stereotype.Component;

@Component
public class MetricsCollectorImpl implements MetricsCollector {
    @Override
    public void incrementCounter(String name) {
        // Simple implementation - can be replaced with Micrometer
        // log.info("Metrics: {}", name);
    }
}
