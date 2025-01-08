package TrackFlow.TaskManagement.controller;

import TrackFlow.TaskManagement.kafkaConfig.KafkaProducer;
import TrackFlow.TaskManagement.model.Task;
import TrackFlow.TaskManagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
//@CrossOrigin("http://localhost:3000")
public class TaskController {

    private final TaskService taskService;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public TaskController(TaskService taskService, KafkaProducer kafkaProducer) {
        this.taskService = taskService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            kafkaProducer.sendTaskCreationMessage(createdTask.getTaskId().toString());
            return ResponseEntity.ok(createdTask);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid Project ID", e);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        try {
            Task updatedTask = taskService.updateTask(taskId, taskDetails);
            kafkaProducer.sendTaskUpdateMessage(updatedTask.getTaskId().toString());
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            System.out.println("Error while updating task: " + e.getMessage());
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Task Not Found or Invalid Project ID", e);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            kafkaProducer.sendTaskDeletionMessage(taskId.toString());
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            System.out.println("Error while deleting task: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Task Not Found or Cannot Be Deleted", e);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Task Not Found");
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users")
    @PreAuthorize("")
    public ResponseEntity<Map<String, Object>> countAllTasksByUserId(@RequestParam String userId) {
        Map<String, Object> taskCounts = taskService.countAllTasksByUserId(userId);
        return ResponseEntity.ok(taskCounts);
    }

    @GetMapping("/project")
    @PreAuthorize("")
    public ResponseEntity<Map<String, Object>> countParameters(@RequestParam Long projectId) {
        Map<String, Object> projectMetrics = taskService.countParameters(projectId);
        return ResponseEntity.ok(projectMetrics);
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<Task> assignUserToTask(@PathVariable Long taskId, @RequestParam String email) {
        kafkaProducer.sendUserAssignmentMessage(taskId.toString(), email);
        return ResponseEntity.ok(taskService.assignUserToTask(taskId, email));
    }

    @PostMapping("/{taskId}/remove")
    public ResponseEntity<Task> removeUserFromTask(@PathVariable Long taskId, @RequestParam String email) {
        kafkaProducer.sendUserRemovalMessage(taskId.toString(), email);
        return ResponseEntity.ok(taskService.removeUserFromTask(taskId, email));
    }

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<?> postComment(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        String commentText = request.get("commentText");
        String userName = request.get("userName");
        taskService.postComment(taskId, commentText, userName);
        return ResponseEntity.ok("Comment posted successfully.");
    }

    @PutMapping("/{taskId}/comments/{commentId}/resolve")
    public ResponseEntity<?> resolveComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        taskService.resolveComment(taskId, commentId, userName);
        return ResponseEntity.ok("Comment resolved successfully.");
    }

    @GetMapping("/users/recent")
    public ResponseEntity<List<Task>> getRecentTasksForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        List<Task> tasks = taskService.getRecentTasksForUser(userId);
        return ResponseEntity.ok(tasks);}
}




