package com.example.oms.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "oms-topic", groupId = "oms-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
