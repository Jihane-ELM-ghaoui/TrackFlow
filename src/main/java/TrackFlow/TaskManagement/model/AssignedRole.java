package TrackFlow.TaskManagement.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
public class AssignedRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AssignedRoleId;
    private Long taskId;
    private Long roleId;

    @ManyToOne
    @JoinColumn(name = "taskId", insertable = false, updatable = false)
    private Task task;
}
