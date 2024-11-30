package TrackFlow.TaskManagement.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
public class AssignedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AssignedUserId;
    private Long taskId;
    private String userId;

    @ManyToOne
    @JoinColumn(name = "taskId", insertable = false, updatable = false)
    private Task task;
}
