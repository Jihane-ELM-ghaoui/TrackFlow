package trackflow.taskmanagement2.bean;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<String> members;

    public ProjectResponse(Long id, String name, String description, LocalDateTime createdAt, List<String> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.members = members;
    }

}
