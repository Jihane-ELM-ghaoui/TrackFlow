package TrackFlow.TaskManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;
    private String userName;
    @Lob
    private String text;
    private LocalDateTime timestamp;
    private boolean resolved;

    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task task;

    @Override
    public String toString() {
        return  userName + ": " + text + "| Timestamp: " + timestamp.toString();
    }
}
