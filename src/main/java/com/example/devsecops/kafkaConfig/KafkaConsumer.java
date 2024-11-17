package com.example.devsecops.kafkaConfig;

import com.example.devsecops.service.impl.NotificationEvent;
import com.example.devsecops.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "EmailVerification-topic")
    public void handleEmailVerificationMessage(String userId) {
        String message = "We’ve sent a verification email to your inbox. Please check your email and click the link to verify your email address.";
        System.out.println("Received Kafka message");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }
}
