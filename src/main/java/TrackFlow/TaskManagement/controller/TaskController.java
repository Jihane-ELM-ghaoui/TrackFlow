package TrackFlow.TaskManagement.controller;

import TrackFlow.TaskManagement.dto.TaskDto;
import TrackFlow.TaskManagement.model.Task;
import TrackFlow.TaskManagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("http://localhost:3000")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public void createTask(@RequestBody TaskDto taskDto) {
        taskService.createTask(taskDto); }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable Long taskId) {
        Optional<Task> task = taskService.getTaskById(taskId);
        return task.orElse(null); }

    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId); }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        return taskService.updateTask(taskId, taskDetails); }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId); }
}




