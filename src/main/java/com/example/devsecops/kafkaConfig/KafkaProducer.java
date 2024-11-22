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


    public void sendEmailVerificationMessage(String userid) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("EmailVerification-topic", userid);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + userid +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            userid + "] due to : " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("ERROR : "+ ex.getMessage());
        }
    }

    public void sendBucketCreateMessage(String userid) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("BucketCreate-topic", userid);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + userid +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            userid + "] due to : " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("ERROR : "+ ex.getMessage());
        }
    }

}
