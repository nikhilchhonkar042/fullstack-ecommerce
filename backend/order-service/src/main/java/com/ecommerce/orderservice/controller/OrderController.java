package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.command.CreateOrderCommand;
import com.ecommerce.orderservice.dto.OrderDto;
import com.ecommerce.orderservice.mapper.OrderMapper;
import com.ecommerce.orderservice.request.CreateOrderRequest;
import com.ecommerce.orderservice.service.HighPerformanceOrderService;
import com.ecommerce.orderservice.service.OrderQueryService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@Validated
@SecurityRequirement(name = "bearer-key")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final HighPerformanceOrderService orderService;
    private final OrderQueryService queryService;
    private final MeterRegistry meterRegistry;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public CompletableFuture<ResponseEntity<OrderDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        Timer.Sample sample = Timer.start(meterRegistry);

        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(UUID.fromString(authentication.getName()))
                .items(request.getItems())
                .shippingAddress(request.getShippingAddress())
                .paymentMethodId(request.getPaymentMethodId())
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        return orderService.createOrderAsync(command)
                .thenApply(result -> {
                    sample.stop(Timer.builder("order.creation.duration")
                            .tag("status", result.isSuccess() ? "success" : "failure")
                            .register(meterRegistry));

                    if (result.isSuccess()) {
                        OrderDto orderDto = OrderMapper.toDto(result.getOrder());
                        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
                    } else {
                        throw new IllegalStateException(result.getErrorMessage());
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Failed to create order for customer: {}", command.getCustomerId(), throwable);
                    sample.stop(Timer.builder("order.creation.duration")
                            .tag("status", "error")
                            .register(meterRegistry));
                    throw new IllegalStateException("Failed to create order", throwable);
                });
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @org.springframework.cache.annotation.Cacheable(value = "orders", key = "#orderId")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable @Valid @NotNull UUID orderId,
            Authentication authentication) {

        OrderDto order = queryService.getOrderById(orderId, authentication);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/status/stream")
    @Operation(summary = "Stream order status updates")
    @PreAuthorize("hasRole('CUSTOMER')")
    public SseEmitter streamOrderStatus(@PathVariable UUID orderId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        orderService.subscribeToOrderUpdates(orderId, emitter);
        return emitter;
    }

    @GetMapping
    @Operation(summary = "Get customer orders with pagination")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderDto>> getCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {

        UUID customerId = UUID.fromString(authentication.getName());
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        Page<OrderDto> orders = queryService.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(orders);
    }}

    return orderService.createOrderAsync(command).thenApply(result->{sample.stop(Timer.builder("order.creation.duration").tag("status",result.isSuccess()?"success":"failure").register(meterRegistry));

    if(result.isSuccess()){

    OrderDto orderDto = OrderMapper
            .toDto(result.getOrder());return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }else{throw new IllegalStateException(result.getErrorMessage());}}).exceptionally(throwable->{log.error("Failed to create order for customer: {}",command.getCustomerId(),throwable);sample.stop(Timer.builder("order.creation.duration").tag("status","error").register(meterRegistry));throw new IllegalStateException("Failed to create order",throwable);});}

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @org.springframework.cache.annotation.Cacheable(value = "orders", key = "#orderId")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable @Valid @NotNull UUID orderId,
            Authentication authentication) {

        OrderDto order = queryService.getOrderById(orderId, authentication);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/status/stream")
    @Operation(summary = "Stream order status updates")
    @PreAuthorize("hasRole('CUSTOMER')")
    public SseEmitter streamOrderStatus(@PathVariable UUID orderId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        orderService.subscribeToOrderUpdates(orderId, emitter);
        return emitter;
    }

    @GetMapping
    @Operation(summary = "Get customer orders with pagination")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderDto>> getCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {

        UUID customerId = UUID.fromString(authentication.getName());
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        Page<OrderDto> orders = queryService.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(orders);
    }
}
