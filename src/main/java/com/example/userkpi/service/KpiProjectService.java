package com.example.userkpi.service;

import com.example.userkpi.DTO.KpiProjectResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KpiProjectService {
    public KpiProjectResponse calculateKpisForProject(
            Long projectId,
            Map<Long, Integer> cycleTime,
            Long totalTasksByProjectId,
            Long onTimeTasksByProjectId,
            Long completedTasksByProjectId) {

        // Calculate On-Time Completion Rate
        double onTimeCompletionRate = (completedTasksByProjectId == 0) ? 0 :
                (double) onTimeTasksByProjectId / completedTasksByProjectId * 100;

        // Map cycle times with safe casting
        List<KpiProjectResponse.TaskCycleTime> cycleTimes = cycleTime.entrySet().stream()
                .map(entry -> new KpiProjectResponse.TaskCycleTime(
                        Long.valueOf(String.valueOf(entry.getKey())),  // Ensure key is safely converted to Long
                        Long.valueOf(String.valueOf(entry.getValue()))  // Ensure value is safely converted to Long
                ))
                .collect(Collectors.toList());

        // Calculate Progress
        double progress = (totalTasksByProjectId == 0) ? 0 :
                (double) completedTasksByProjectId / totalTasksByProjectId * 100;

        return new KpiProjectResponse(onTimeCompletionRate, cycleTimes, progress);
    }
}
