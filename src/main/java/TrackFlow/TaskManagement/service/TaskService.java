package TrackFlow.TaskManagement.service;

import TrackFlow.TaskManagement.dto.TaskDto;
import TrackFlow.TaskManagement.model.AssignedRole;
import TrackFlow.TaskManagement.model.AssignedUser;
import TrackFlow.TaskManagement.model.Task;
import TrackFlow.TaskManagement.repository.TaskRepository;
import TrackFlow.TaskManagement.repository.AssignedRoleRepository;
import TrackFlow.TaskManagement.repository.AssignedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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

    public void createTask(TaskDto taskDto) {
        // Create and populate the Task entity
        Task task = new Task();
        task.setTaskName(taskDto.getTaskName());
        task.setTaskDescription(taskDto.getTaskDescription());
        task.setTaskPriority(taskDto.getTaskPriority());
        task.setTaskStartDate(taskDto.getTaskStartDate());
        task.setTaskEstimatedEndDate(taskDto.getTaskEstimatedEndDate());
        task.setTaskEndDate(taskDto.getTaskEndDate());
        task.setCreatedBy(taskDto.getCreatedBy());
        task.setCreatedAt(LocalDateTime.now());
        task.setProjectId(taskDto.getProjectId());
        Task.Status taskStatus = Task.Status.valueOf(taskDto.getStatus().name());
        task.setStatus(taskStatus);        taskRepository.save(task);
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
        task.setTaskStartDate(taskDetails.getTaskStartDate());
        task.setTaskEndDate(taskDetails.getTaskEndDate());
        task.setTaskPriority(taskDetails.getTaskPriority());

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

}
