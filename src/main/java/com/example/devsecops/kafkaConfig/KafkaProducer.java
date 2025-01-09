package com.example.devsecops.kafkaConfig;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;



    public void sendChatMessage(String sendername, String receiver) {
        try {
            String message = sendername + "-" + receiver;
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("Chat-topic", message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + message +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            message + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            System.out.println("ERROR : " + ex.getMessage());
        }
    }





}
