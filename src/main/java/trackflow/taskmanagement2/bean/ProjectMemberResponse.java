package trackflow.taskmanagement2.bean;

public class ProjectMemberResponse {
    private Long id;
    private String projectName;
    private String email;

    public ProjectMemberResponse(Long id, String projectName, String email) {
        this.id = id;
        this.projectName = projectName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
