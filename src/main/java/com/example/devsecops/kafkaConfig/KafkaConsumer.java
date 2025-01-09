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

    @KafkaListener(topics = "TaskCreation-topic")
    public void handleTaskCreationMessage(String userId) {
        String message = "A new task has been created!";
        System.out.println("Received Kafka message: Task created");

        System.out.println("userId"+ userId);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "TaskUpdate-topic")
    public void handleTaskUpdateMessage(String userId) {
        String message = "A task has been updated!";
        System.out.println("Received Kafka message: Task updated");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "TaskDeletion-topic")
    public void handleTaskDeletionMessage(String userId) {
        String message = "A task has been deleted!";
        System.out.println("Received Kafka message: Task deleted");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "TaskAssignment-topic")
    public void handleTaskAssignmentMessage(String userId) {
        String message = "A task has been assigned to you!";
        System.out.println("Received Kafka message: Task assigned");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "TaskRemoval-topic")
    public void handleTaskRemovalMessage(String userId) {
        String message = "You have been removed from a task!";
        System.out.println("Received Kafka message: Task removal");

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "CommentPosted-topic")
    public void handleCommentPosted(String message) {
        System.out.println("Received Kafka message: " + message);
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    @KafkaListener(topics = "CommentResolved-topic")
    public void handleCommentResolved(String message) {
        System.out.println("Received Kafka message: " + message);

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

}
