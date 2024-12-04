package trackflow.taskmanagement2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import trackflow.taskmanagement2.bean.MemberResponse;
import trackflow.taskmanagement2.bean.Project;
import trackflow.taskmanagement2.bean.ProjectResponse;
import trackflow.taskmanagement2.bean.ProjectMember;
import trackflow.taskmanagement2.dao.ProjectMemberRepository;
import trackflow.taskmanagement2.dao.ProjectRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public Project createProject(Project project) {
        if (project.getName() == null || project.getName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty.");
        }

        if (project.getDescription() == null || project.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Project description cannot be null or empty.");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("User must be authenticated to create a project.");
        }
        project.setCreatedAt(LocalDateTime.now());
        String leaderId = auth.getName();
        project.setLeaderid(leaderId);
        return projectRepository.save(project);
    }

    public Project getProjectByName(String projectName) {
        return projectRepository.findByName(projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
    public List<Project> getProjectByLeaderId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("User must be authenticated to retrieve a project.");
        }


        // Set the leader ID from the authentication object
        String leaderId = auth.getName();

        // Fetch the project using the leader ID
        List<Project> project = projectRepository.findByLeaderid(leaderId);

        // Handle the case where no project is found
        if (project == null) {
            throw new IllegalStateException("No project found for the given leader.");
        }

        return project;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    public List<Project> getAllProjectByLeaderId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("User must be authenticated to retrieve a project.");
        }

        String userId = auth.getName();

        List<Project> project = projectRepository.findByLeaderid(userId);

        if (project == null) {
            throw new IllegalStateException("No project found for the given leader.");
        }

        return project;
    }

    public ProjectResponse getProjectDetails(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<String> memberEmails = projectMemberRepository.findByProjectAndStatus(project, "Joined")
                .stream()
                .map(ProjectMember::getEmail)
                .collect(Collectors.toList());

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                memberEmails
        );
    }
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        List<MemberResponse> members = projectMemberRepository.findByProjectAndStatus(project, "Joined")
                .stream()
                .map(member -> new MemberResponse(member.getEmail(), member.getStatus()))
                .collect(Collectors.toList());

        return members;
    }
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}
