package TrackFlow.TaskManagement.controller;

import TrackFlow.TaskManagement.model.AssignedRole;
import TrackFlow.TaskManagement.model.AssignedUser;
import TrackFlow.TaskManagement.model.Task;
import TrackFlow.TaskManagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Task createdTask = taskService.createTask(task, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDetails));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/assign-user")
    public ResponseEntity<AssignedUser> assignUserToTask(@PathVariable Long taskId, @RequestParam String userId) {
        return ResponseEntity.ok(taskService.assignUserToTask(taskId, userId));
    }

    @PostMapping("/{taskId}/assign-role")
    public ResponseEntity<AssignedRole> assignRoleToTask(@PathVariable Long taskId, @RequestParam Long roleId) {
        return ResponseEntity.ok(taskService.assignRoleToTask(taskId, roleId));
    }
}

