package trackflow.taskmanagement2.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trackflow.taskmanagement2.bean.*;
import trackflow.taskmanagement2.dao.ProjectMemberRepository;
import trackflow.taskmanagement2.dao.ProjectRepository;
import trackflow.taskmanagement2.dao.RoleRepository;
import java.util.UUID;


import java.util.List;
import java.util.Optional;

@Service
public class ProjectMemberService {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    public ProjectMember addMemberToProject(String projectName, String email) {
        Project project = projectRepository.findByName(projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        //  unique invitation token
        String token = UUID.randomUUID().toString();

        ProjectMember projectMember = new ProjectMember();
        projectMember.setEmail(email);            // Member's Gmail
        projectMember.setProject(project);        // Associate with the project
        projectMember.setInvitationToken(token);  // Save the unique token
        projectMember.setStatus("Pending");       // Set initial status as "Pending"

        projectMember = projectMemberRepository.save(projectMember);

        sendInvitationEmail(email, projectName, token);

        return projectMember;
    }
    private void sendInvitationEmail(String email, String projectName, String token) {
        String joinLink = "http://localhost:3000/projects/join?token=" + token;

        String subject = "You're invited to join the project: " + projectName;
        String body = "Hello,\n\nYou've been invited to join the project '" + projectName + "'.\n" +
                "Click the link below to join:\n" + joinLink +
                "\n\nJoin and do your best !";

        emailService.sendEmail(email, subject, body);
    }

    public ProjectMember assignRoleToMember(String projectName, String email, String roleName) {
        ProjectMember projectMember = projectMemberRepository.findByProjectNameAndEmail(projectName, email)
                .orElseThrow(() -> new RuntimeException("Project member with email " + email + " not found in project " + projectName));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));

        projectMember.setRole(role);
        return projectMemberRepository.save(projectMember);
    }
    public Project handleProjectJoin(String token) {
        ProjectMember projectMember = projectMemberRepository.findByInvitationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (!"Pending".equals(projectMember.getStatus())) {
            throw new RuntimeException("This invitation has already been used or is invalid.");
        }

        projectMember.setStatus("Joined");
        projectMemberRepository.save(projectMember);

        Project project = projectMember.getProject();
        return project;
    }

    public Project joinProjectWithToken(String token) {
        ProjectMember projectMember = projectMemberRepository.findByInvitationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (!"Pending".equals(projectMember.getStatus())) {
            throw new RuntimeException("Invitation already used");
        }

        projectMember.setStatus("Joined");
        projectMemberRepository.save(projectMember);

        Project project = projectMember.getProject();
        project.getProjectMembers().add(projectMember);
        projectRepository.save(project);

        return project;
    }


}


