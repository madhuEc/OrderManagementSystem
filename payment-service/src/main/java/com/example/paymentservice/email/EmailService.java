package com.example.paymentservice.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void sendPaymentConfirmation(String orderId, boolean success) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("user@example.com"); // replace with real recipient or placeholder
        message.setSubject("Payment " + (success ? "Success" : "Failure") + " for Order " + orderId);
        message.setText("Your payment for order " + orderId + " was " + (success ? "successful" : "unsuccessful") + ".");
        mailSender.send(message);
    }
}
