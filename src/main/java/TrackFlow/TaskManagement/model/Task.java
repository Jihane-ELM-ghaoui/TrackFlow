package TrackFlow.TaskManagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @JsonFormat
    private String taskName;

    @JsonFormat
    private String taskDescription;

    @JsonFormat
    private int taskPriority;

    @JsonFormat
    private Long projectId;

    @ElementCollection
    private List<String> assignedUsers = new ArrayList<>();


    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskEstimatedEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskEndDate;

    @JsonFormat
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @JsonFormat
    private Status status;
    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public String getTaskIdString() {
        return String.valueOf(taskId); // Convert Long taskId to String
    }

    @Override
    public String toString() {
        StringBuilder commentsString = new StringBuilder();
        for (Comment comment : comments) {
            commentsString.append(comment.toString()).append("\n"); // Each comment on a new line
        }
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskPriority=" + taskPriority +
                ", projectId=" + projectId +
                ", assignedUsers=" + assignedUsers +
                ", taskStartDate=" + taskStartDate +
                ", taskEstimatedEndDate=" + taskEstimatedEndDate +
                ", taskEndDate=" + taskEndDate +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", status=" + status +
                '}'+
                ", comments=\n" + commentsString.toString() + // Add comments to the string
                '}';
    }

}