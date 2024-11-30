package TrackFlow.TaskManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class Project {
    @Id
    private Long id;
    private String leaderid;
}
