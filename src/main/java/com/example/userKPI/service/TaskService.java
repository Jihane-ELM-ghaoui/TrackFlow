package com.example.userKPI.service;

import com.example.userKPI.repo.TaskRepo;
import com.example.userKPI.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private KpiProjectService kpiProjectService;
    @Autowired
    private KpiService kpiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Task addTask(Task task) {
        Task savedTask = taskRepo.save(task);
        kpiProjectService.recalculateKpiAndNotify(savedTask.getProjectId());// Trigger KPI update
        kpiService.recalculateKpiForUserAndNotify();
        messagingTemplate.convertAndSend("/topic/taskUpdates", savedTask); // Notify clients of new task
        return savedTask;
    }

    public Task updateTask(Long taskId, Task updatedTaskData) {
        Task existingTask = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Update task fields
        existingTask.setTaskName(updatedTaskData.getTaskName());
        existingTask.setStatus(updatedTaskData.getStatus());
        existingTask.setProjectId(updatedTaskData.getProjectId());
        existingTask.setAssignedUser(updatedTaskData.getAssignedUser());
        existingTask.setTaskStartDate(updatedTaskData.getTaskStartDate());
        existingTask.setTaskEstimatedEndDate(updatedTaskData.getTaskEstimatedEndDate());
        existingTask.setTaskEndDate(updatedTaskData.getTaskEndDate());

        Task savedTask = taskRepo.save(existingTask);

        kpiProjectService.recalculateKpiAndNotify(savedTask.getProjectId()); // Trigger KPI update
        kpiService.recalculateKpiForUserAndNotify();
        messagingTemplate.convertAndSend("/topic/taskUpdates", savedTask); // Notify clients of task update

        return savedTask;
    }
    public void deleteTask(Long taskId) {
        Task taskToDelete = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Long projectId = taskToDelete.getProjectId();

        // Delete the task from the repository
        taskRepo.deleteById(taskId);

        // Recalculate and broadcast updated KPIs
        kpiProjectService.recalculateKpiAndNotify(projectId);
        kpiService.recalculateKpiForUserAndNotify();

        // Notify clients about the task deletion
        messagingTemplate.convertAndSend("/topic/taskUpdates", taskId); // Send only the task ID to indicate deletion
    }

}
