package TrackFlow.TaskManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;


    private String taskName;
    private String taskDescription;
    private int taskPriority;
    private Long projectId;
    private Date taskStartDate;
    private Date taskEstimatedEndDate;
    private Date taskEndDate;
    private LocalDateTime createdAt;
    private String createdBy;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
