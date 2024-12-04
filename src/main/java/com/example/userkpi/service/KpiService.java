package com.example.userkpi.service;

import com.example.userkpi.DTO.KpiResponse;
import com.example.userkpi.repo.TaskRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KpiService {

    private final TaskRepo taskRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public KpiService(TaskRepo taskRepository, SimpMessagingTemplate messagingTemplate) {
        this.taskRepository = taskRepository;
        this.messagingTemplate=messagingTemplate;
    }

    public KpiResponse calculateKpisForUser() {
        // Retrieve the current authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extract the userId from the authentication object
        String userId = auth.getName();
        System.out.println("Authenticated User: " + userId);

        int totalTasks = taskRepository.countAllTasksByUserId(userId);
        int completedTasks = taskRepository.countCompletedTasksByUserId(userId);
        int incompleteTasks = taskRepository.countIncompleteTasksByUserId(userId);

        Map<String, Long> statusCount = new HashMap<>();
        taskRepository.countTasksByStatus(userId).forEach(entry -> {
            String status = entry[0].toString();
            Long count = (Long) entry[1];

            // Map the status values to match frontend expectations
            if (status.equals("NOT_STARTED")) {
                statusCount.put("notStarted", count);
            } else if (status.equals("IN_PROGRESS")) {
                statusCount.put("inProgress", count);
            } else if (status.equals("COMPLETED")) {
                statusCount.put("completed", count);
            }
        });

        double taskCompletionRate = totalTasks == 0 ? 0 : (completedTasks * 100.0) / totalTasks;
        double incompleteTaskRate = totalTasks == 0 ? 0 : (incompleteTasks * 100.0) / totalTasks;

        return new KpiResponse(taskCompletionRate, incompleteTaskRate, statusCount);
    }
    public void recalculateKpiForUserAndNotify() {
        KpiResponse kpiResponse = calculateKpisForUser();
        messagingTemplate.convertAndSend("/topic/kpiUpdates", kpiResponse);
    }

}