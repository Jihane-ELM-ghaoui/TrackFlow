package trackflow.taskmanagement2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackflow.taskmanagement2.bean.MemberResponse;
import trackflow.taskmanagement2.bean.Project;
import trackflow.taskmanagement2.bean.ProjectResponse;
import trackflow.taskmanagement2.dao.ProjectRepository;
import trackflow.taskmanagement2.service.Auth0Service;
import trackflow.taskmanagement2.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private Auth0Service auth0Service;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjectByLeaderId();
            return ResponseEntity.ok(projects);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping("/name/{projectName}")
    public Project getProject(@PathVariable String projectName) {
        return projectService.getProjectByName(projectName);
    }
    @GetMapping("/id/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectDetails(@PathVariable Long projectId) {
        ProjectResponse projectResponse = projectService.getProjectDetails(projectId);
        return ResponseEntity.ok(projectResponse);
    }
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        List<MemberResponse> members = projectService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long id) {
        Project project = projectService.getProjectById(id);
        if (project != null) {
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
