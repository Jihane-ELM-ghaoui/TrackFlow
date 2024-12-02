package TrackFlow.TaskManagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class TaskDto {
    private String taskName;
    private String taskDescription;
    private Integer taskPriority;
    private Date taskStartDate;
    private Date taskEstimatedEndDate;
    private Date taskEndDate;
    private String createdBy;
    private Long projectId;
    private Status status;

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

}
