package TrackFlow.TaskManagement.service;

import TrackFlow.TaskManagement.model.AssignedRole;
import TrackFlow.TaskManagement.model.AssignedUser;
import TrackFlow.TaskManagement.model.Project;
import TrackFlow.TaskManagement.model.Task;
import TrackFlow.TaskManagement.repository.TaskRepository;
import TrackFlow.TaskManagement.repository.AssignedRoleRepository;
import TrackFlow.TaskManagement.repository.AssignedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AssignedUserRepository assignedUserRepository;

    @Autowired
    private AssignedRoleRepository assignedRoleRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Task createTask(Task task, Long userId) {
        String projectServiceUrl = "http://localhost:8091/api/projects/" + task.getProjectId();
        ResponseEntity<Project> response = restTemplate.getForEntity(projectServiceUrl, Project.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Project project = response.getBody();
            task.setProjectId(project.getId());
            task.setLeaderId(project.getLeaderid());
            task.setCreatedBy(userId);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Failed to fetch project details.");
        }
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTaskName(taskDetails.getTaskName());
        task.setTaskDescription(taskDetails.getTaskDescription());
        task.setTaskStatus(taskDetails.getTaskStatus());
        task.setTaskStartDate(taskDetails.getTaskStartDate());
        task.setTaskEndDate(taskDetails.getTaskEndDate());
        task.setTaskPriority(taskDetails.getTaskPriority());
        task.setTaskVisibility(taskDetails.getTaskVisibility());

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public AssignedUser assignUserToTask(Long taskId, String userId) {
        AssignedUser assignedUser = new AssignedUser();
        assignedUser.setTaskId(taskId);
        assignedUser.setUserId(userId);
        return assignedUserRepository.save(assignedUser);
    }

    public AssignedRole assignRoleToTask(Long taskId, Long roleId) {
        AssignedRole assignedRole = new AssignedRole();
        assignedRole.setTaskId(taskId);
        assignedRole.setRoleId(roleId);
        return assignedRoleRepository.save(assignedRole);
    }
}
