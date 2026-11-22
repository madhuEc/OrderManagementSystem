package com.example.orderservice.kafka;

import com.example.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
        System.out.println("Order Service: Published OrderCreatedEvent to " + TOPIC + " [OrderId: " + event.getOrderId() + ", Item: " + event.getItemId() + ", Qty: " + event.getQuantity() + "]");
    }
}
