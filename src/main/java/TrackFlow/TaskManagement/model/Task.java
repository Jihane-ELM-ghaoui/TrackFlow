package TrackFlow.TaskManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private Long projectId;
    private String leaderId;
    private String taskName;
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    public enum TaskStatus {
        IN_PROGRESS, DONE, PAUSED
    }

    private Date taskStartDate;
    private Date taskEndDate;
    private int taskPriority;
    private String taskVisibility;
    private Long createdBy;

    @ManyToOne
    @JoinColumn(name = "projectId", referencedColumnName = "id", insertable = false, updatable = false)
    private Project project;
}
