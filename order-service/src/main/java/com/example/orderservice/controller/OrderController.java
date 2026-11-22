package com.example.orderservice.controller;

import com.example.common.events.OrderCreatedEvent;
import com.example.orderservice.kafka.OrderKafkaProducer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderKafkaProducer kafkaProducer;

    private final List<OrderCreatedEvent> orders = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<OrderCreatedEvent>> getOrders() {
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .itemId(request.getItemId())
                .quantity(request.getQuantity())
                .build();
        // store for GET endpoint
        orders.add(event);
        kafkaProducer.publishOrderCreated(event);
        return ResponseEntity.ok(orderId);
    }

    @Data
    public static class CreateOrderRequest {
        private String itemId;
        private int quantity;
    }
}
