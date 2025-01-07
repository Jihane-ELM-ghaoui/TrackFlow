package ma.ensa.StorageManager.kafkaConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendFileUploadMessage(String userId) {
        sendMessage("FileUpload-topic", userId);
    }

    public void sendFileDownloadMessage(String userId) {
        sendMessage("FileDownload-topic", userId);
    }

    public void sendFileDeleteMessage(String userId) {
        sendMessage("FileDelete-topic", userId);
    }

    private void sendMessage(String topic, String userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth != null ? auth.getName() : "Anonymous";

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, userId);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("User [" + currentUser + "] sent message=[" + userId +
                            "] to topic=[" + topic + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("User [" + currentUser + "] failed to send message=[" + userId +
                            "] to topic=[" + topic + "] due to: " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.err.println("Unexpected error while sending message to topic=[" + topic + "]: " + ex.getMessage());
        }
    }
}
