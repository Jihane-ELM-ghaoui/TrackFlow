package TrackFlow.TaskManagement.kafkaConfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic createTaskCreationTopic() {
        return new NewTopic("TaskCreation-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic createTaskUpdateTopic() {
        return new NewTopic("TaskUpdate-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic createTaskDeletionTopic() {
        return new NewTopic("TaskDeletion-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic createTaskAssignmentTopic() {
        return new NewTopic("TaskAssignment-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic createTaskRemovalTopic() {
        return new NewTopic("TaskRemoval-topic", 1, (short) 1);
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic commentPostedTopic() {
        return new NewTopic("CommentPosted-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic commentResolvedTopic() {
        return new NewTopic("CommentResolved-topic", 1, (short) 1);
    }
}
