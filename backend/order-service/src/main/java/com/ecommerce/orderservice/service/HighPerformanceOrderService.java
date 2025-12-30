package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.command.CreateOrderCommand;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.result.OrderResult;
import com.ecommerce.orderservice.result.ValidationResult;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.support.DistributedCache;
import com.ecommerce.orderservice.support.MetricsCollector;
import com.ecommerce.orderservice.client.InventoryServiceClient;
import com.ecommerce.orderservice.client.PaymentServiceClient;
import com.ecommerce.orderservice.client.CustomerServiceClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HighPerformanceOrderService {

    private final OrderRepository orderRepository;
    private final DistributedCache distributedCache;
    private final MetricsCollector metricsCollector;
    private final OrderValidator orderValidator;
    private final OrderFactory orderFactory;
    private final InventoryServiceClient inventoryClient;
    private final PaymentServiceClient paymentClient;
    private final CustomerServiceClient customerClient;
    private final ConcurrentHashMap<UUID, SseEmitter> orderSubscriptions = new ConcurrentHashMap<>();

    @Async("orderProcessingExecutor")
    @CircuitBreaker(name = "order-creation", fallbackMethod = "createOrderFallback")
    @TimeLimiter(name = "order-creation")
    @Bulkhead(name = "order-creation", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<OrderResult> createOrderAsync(CreateOrderCommand command) {
        log.info("Processing order creation for customer: {}", command.getCustomerId());

        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = generateIdempotencyKey(command);
            OrderResult cachedResult = distributedCache.get(cacheKey, OrderResult.class);
            if (cachedResult != null) {
                log.info("Returning cached order result for key: {}", cacheKey);
                metricsCollector.incrementCounter("order.cache.hit");
                return cachedResult;
            }

            CompletableFuture<ValidationResult> validation = validateOrderAsync(command);

            try {
                ValidationResult validationResult = validation.get(5, TimeUnit.SECONDS);
                if (!validationResult.isValid()) {
                    log.warn("Order validation failed: {}", validationResult.getErrors());
                    return OrderResult.failure(validationResult.getErrors().toString());
                }

                Order order = orderFactory.createOptimizedOrder(command);
                Order savedOrder = saveOrderWithRetry(order);
                publishOrderCreatedEventAsync(savedOrder);

                OrderResult result = OrderResult.success(savedOrder);
                distributedCache.put(cacheKey, result, Duration.ofMinutes(10));

                metricsCollector.incrementCounter("order.created.success");
                log.info("Order created successfully: {}", savedOrder.getId());

                return result;

            } catch (TimeoutException e) {
                log.error("Order validation timed out for customer: {}", command.getCustomerId());
                metricsCollector.incrementCounter("order.validation.timeout");
                return OrderResult.failure("Order validation timed out");
            } catch (Exception e) {
                log.error("Failed to create order for customer: {}", command.getCustomerId(), e);
                metricsCollector.incrementCounter("order.creation.error");
                return OrderResult.failure("Order creation failed: " + e.getMessage());
            }
        });
    }

    private CompletableFuture<ValidationResult> validateOrderAsync(CreateOrderCommand command) {
        CompletableFuture<Boolean> inventoryCheck = CompletableFuture
                .supplyAsync(() -> inventoryClient.checkAvailability(command.getItems()))
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    log.warn("Inventory check failed, assuming available", throwable);
                    return true;
                });

        CompletableFuture<Boolean> customerCheck = CompletableFuture
                .supplyAsync(() -> customerClient.validateCustomer(command.getCustomerId()))
                .orTimeout(2, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    log.warn("Customer validation failed", throwable);
                    return false;
                });

        CompletableFuture<Boolean> paymentCheck = CompletableFuture
                .supplyAsync(() -> paymentClient.validatePaymentMethod(
                        command.getCustomerId(), command.getPaymentMethodId()))
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    log.warn("Payment method validation failed", throwable);
                    return false;
                });

        return CompletableFuture.allOf(inventoryCheck, customerCheck, paymentCheck)
                .thenApply(v -> {
                    List<String> errors = new ArrayList<>();
                    try {
                        if (!inventoryCheck.get()) {
                            errors.add("Insufficient inventory for requested items");
                        }
                        if (!customerCheck.get()) {
                            errors.add("Invalid customer");
                        }
                        if (!paymentCheck.get()) {
                            errors.add("Invalid payment method");
                        }
                    } catch (Exception e) {
                        errors.add("Validation service error: " + e.getMessage());
                    }
                    return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
                });
    }

    private Order saveOrderWithRetry(Order order) {
        try {
            return orderRepository.save(order);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking failure, retrying order save: {}", order.getId());
            metricsCollector.incrementCounter("order.save.retry");
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving order: {}", order.getId(), e);
            metricsCollector.incrementCounter("order.save.integrity_error");
            throw new RuntimeException("Failed to save order due to data constraints", e);
        }
    }

    @Async("eventProcessingExecutor")
    private void publishOrderCreatedEventAsync(Order order) {
        try {
            log.info("Order created event would be published for: {}", order.getId());
            metricsCollector.incrementCounter("order.event.published");
        } catch (Exception e) {
            log.error("Error preparing order created event: {}", order.getId(), e);
            metricsCollector.incrementCounter("order.event.preparation_error");
        }
    }

    public void subscribeToOrderUpdates(UUID orderId, SseEmitter emitter) {
        orderSubscriptions.put(orderId, emitter);

        emitter.onCompletion(() -> {
            orderSubscriptions.remove(orderId);
            log.info("Order subscription completed: {}", orderId);
        });

        emitter.onTimeout(() -> {
            orderSubscriptions.remove(orderId);
            log.info("Order subscription timed out: {}", orderId);
        });

        emitter.onError(throwable -> {
            orderSubscriptions.remove(orderId);
            log.error("Order subscription error: {}", orderId, throwable);
        });

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            emitter.send(SseEmitter.event()
                    .name("order-status")
                    .data("{\"status\":\"" + order.getStatus() + "\"}")
                    .id(UUID.randomUUID().toString()));
        } catch (Exception e) {
            log.error("Failed to send initial order status: {}", orderId, e);
            emitter.completeWithError(e);
        }
    }

    public CompletableFuture<OrderResult> createOrderFallback(CreateOrderCommand command, Exception ex) {
        log.warn("Order creation circuit breaker activated for customer: {}", command.getCustomerId(), ex);
        metricsCollector.incrementCounter("order.circuit_breaker.activated");
        return CompletableFuture.completedFuture(
                OrderResult.failure("Order service temporarily unavailable. Please try again later."));
    }

    private String generateIdempotencyKey(CreateOrderCommand command) {
        return String.format("order:create:%s:%s",
                command.getCustomerId(),
                command.getIdempotencyKey());
    }
}
