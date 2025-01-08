package TrackFlow.TaskManagement.kafkaConfig;

import TrackFlow.TaskManagement.model.Comment;
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

    private static final String TASK_CREATION_TOPIC = "TaskCreation-topic";
    private static final String TASK_UPDATE_TOPIC = "TaskUpdate-topic";
    private static final String TASK_DELETION_TOPIC = "TaskDeletion-topic";
    private static final String TASK_ASSIGNMENT_TOPIC = "TaskAssignment-topic";
    private static final String TASK_REMOVAL_TOPIC = "TaskRemoval-topic";
    private static final String COMMENT_POSTED_TOPIC = "CommentPosted-topic";
    private static final String COMMENT_RESOLVED_TOPIC = "CommentResolved-topic";

    public void sendTaskCreationMessage(String taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        sendMessage(TASK_CREATION_TOPIC, taskId, userId);
    }

    public void sendTaskUpdateMessage(String taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        sendMessage(TASK_UPDATE_TOPIC, taskId, userId);
    }

    public void sendTaskDeletionMessage(String taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        sendMessage(TASK_DELETION_TOPIC, taskId, userId);
    }

    public void sendUserAssignmentMessage(String taskId, String userEmail) {
        sendMessage(TASK_ASSIGNMENT_TOPIC, userEmail, "User assigned to task: " + taskId);
    }

    public void sendUserRemovalMessage(String taskId, String userEmail) {
        sendMessage(TASK_REMOVAL_TOPIC, userEmail, "User removed from task: " + taskId);
    }

    public void sendCommentPostedMessage(Long taskId, String commentText, String userName) {
        String message = String.format("User %s added a comment to Task %d: %s", userName, taskId, commentText);
        sendMessage(COMMENT_POSTED_TOPIC, taskId.toString(), message);
    }

    public void sendCommentResolvedMessage(Long taskId, String commentText, String userName) {
        String message = String.format("User %s resolved a comment on Task %d: %s", userName, taskId, commentText);
        sendMessage(COMMENT_RESOLVED_TOPIC, taskId.toString(), message);
    }

    private void sendMessage(String topic, String key, String message) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth != null ? auth.getName() : "Anonymous";

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("User [" + currentUser + "] sent message=[" + message +
                            "] to topic=[" + topic + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.err.println("User [" + currentUser + "] failed to send message=[" + message +
                            "] to topic=[" + topic + "] due to: " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.err.println("Unexpected error while sending message to topic=[" + topic + "]: " + ex.getMessage());
        }
    }
}
