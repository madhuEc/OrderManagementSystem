
package com.example.inventoryservice.kafka;

import com.example.common.events.InventoryCheckedEvent;
import com.example.common.events.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryListener {

    private final KafkaTemplate<String, InventoryCheckedEvent> kafkaTemplate;

    private static final String INVENTORY_TOPIC = "inventory-events";

    // Simple in-memory inventory store: itemId -> available quantity
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();

    public InventoryListener(
            KafkaTemplate<String, InventoryCheckedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;

        // Pre-populate inventory for demo
        inventory.put("A1", 10);
        inventory.put("B2", 5);
        inventory.put("C3", 0);
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "inventory-service-group"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {

        Integer available = inventory.getOrDefault(event.getItemId(), 0);

        boolean inStock = available >= event.getQuantity();

        // Decrement stock if available
        if (inStock) {
            inventory.put(event.getItemId(),
                    available - event.getQuantity());
            System.out.println("Inventory Service: Item " + event.getItemId() + " is in stock. Remaining: " + (available - event.getQuantity()));
        } else {
            System.out.println("Inventory Service: Item " + event.getItemId() + " is OUT of stock! Requested: " + event.getQuantity() + ", Available: " + available);
        }

        InventoryCheckedEvent checked = InventoryCheckedEvent.builder()
                .orderId(event.getOrderId())
                .inStock(inStock)
                .build();

        kafkaTemplate.send(INVENTORY_TOPIC, event.getOrderId(), checked);
        System.out.println("Published InventoryCheckedEvent to " + INVENTORY_TOPIC + " for order: " + event.getOrderId());
    }
}
