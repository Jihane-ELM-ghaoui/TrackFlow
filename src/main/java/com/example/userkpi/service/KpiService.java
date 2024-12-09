package com.example.userkpi.service;

import com.example.userkpi.DTO.KpiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KpiService {
    public KpiResponse calculateKpisForUser(int totalTasks, int completedTasks, int incompleteTasks, Map<String, Integer> tasksByStatus) {
        // Retrieve the current authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extract the userId from the authentication object
        String userId = auth.getName();
        System.out.println("Authenticated User: " + userId);

        Map<String, Integer> statusCount = new HashMap<>();
        tasksByStatus.forEach((status, count) -> {
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
}

