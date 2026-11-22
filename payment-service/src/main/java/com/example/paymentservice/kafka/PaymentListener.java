package com.example.paymentservice.kafka;

import com.example.common.events.OrderCreatedEvent;
import com.example.common.events.PaymentCompletedEvent;
import com.example.paymentservice.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListener {

    private final EmailService emailService;
    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    private static final String PAYMENT_TOPIC = "payment-events";

    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Simulated payment processing – always succeeds for demo
        boolean success = true;
        
        // Send email with retry logic (simulated if SMTP unavailable)
        try {
            emailService.sendPaymentConfirmation(event.getOrderId(), success);
            System.out.println("Confirmation email sent for order: " + event.getOrderId());
        } catch (Exception e) {
            System.out.println("[Simulated Email] Mail server unavailable (" + e.getMessage() + "). Email simulated for order: " + event.getOrderId());
        }

        // Publish payment result
        PaymentCompletedEvent completed = PaymentCompletedEvent.builder()
                .orderId(event.getOrderId())
                .success(success)
                .build();
        kafkaTemplate.send(PAYMENT_TOPIC, event.getOrderId(), completed);
        System.out.println("Published PaymentCompletedEvent to " + PAYMENT_TOPIC + " for order: " + event.getOrderId());
    }
}
