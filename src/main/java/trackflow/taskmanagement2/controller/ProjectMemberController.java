package trackflow.taskmanagement2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackflow.taskmanagement2.bean.EmailService;
import trackflow.taskmanagement2.bean.ProjectMember;
import trackflow.taskmanagement2.bean.ProjectMemberResponse;
import trackflow.taskmanagement2.dao.ProjectMemberRepository;
import trackflow.taskmanagement2.service.Auth0Service;
import trackflow.taskmanagement2.service.ProjectMemberService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/projectmembers")
public class ProjectMemberController {
    @Autowired
    private ProjectMemberService projectMemberService;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Auth0Service auth0Service;

    @PostMapping("/add")
    public ResponseEntity<ProjectMemberResponse> addProjectMember(@RequestParam String projectName, @RequestParam String email) {
        ProjectMember projectMember = projectMemberService.addMemberToProject(projectName, email);
        return ResponseEntity.ok(new ProjectMemberResponse(projectMember.getId(), projectName, email));
    }




    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleToMember(@RequestParam String projectName, @RequestParam String email, @RequestParam String roleName) {
        System.out.println("Received projectName: " + projectName);
        System.out.println("Received email: " + email);
        System.out.println("Received roleName: " + roleName);

        ProjectMember projectMember = projectMemberService.assignRoleToMember(projectName, email, roleName);

        Map<String, Object> response = new HashMap<>();
        response.put("roleName", roleName);
        response.put("message", "Role assigned successfully");

        return ResponseEntity.ok(response);
    }


}
