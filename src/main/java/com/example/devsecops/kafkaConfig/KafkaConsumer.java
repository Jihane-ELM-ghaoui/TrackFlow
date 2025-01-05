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

    @KafkaListener(topics = "BucketCreate-topic")
    public void handleBucketCreateMessage(String userId) {
        String message = "Your AWS S3 bucket has been created successfully and is ready for use.";
        System.out.println("Received Kafka message : Bucket created");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }


     @KafkaListener(topics = "FileUpload-topic")
    public void handleFileUploadMessage(String userId) {
        String message = "File uploaded successfully !";
        System.out.println("Received Kafka message : File uploaded");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "FileDownload-topic")
    public void handleFileDownloadMessage(String userId) {
        String message = "File downloaded successfully !";
        System.out.println("Received Kafka message : File downloaded");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "FileDelete-topic")
    public void handleFileDeleteMessage(String userId) {
        String message = "File removed successfully !";
        System.out.println("Received Kafka message : File deleted");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }


    @KafkaListener(topics = "Chat-topic")
    public void handleChatMessage(String message) {
        try {
            // Split the message using the delimiter
            String[] parts = message.split("-");
            if (parts.length == 2) {
                String sendername = parts[0];
                String receiver = parts[1];

                System.out.println("Received Kafka message with sender: " + sendername + ", receiver: " + receiver);

                String notificationMessage = "You have a new message from " + sendername;

                Notification notification = new Notification();
                notification.setMessage(notificationMessage);
                notification.setUserId(receiver);
                notification.setTimestamp(LocalDateTime.now());

                eventPublisher.publishEvent(new NotificationEvent(this, notification));
            } else {
                System.out.println("Invalid message format: " + message);
            }
        } catch (Exception ex) {
            System.out.println("Error processing message: " + ex.getMessage());
        }
    }


}
