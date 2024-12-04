package com.example.userkpi.service;

import com.example.userkpi.DTO.KpiProjectResponse;
import com.example.userkpi.repo.TaskRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KpiProjectService {
    private final TaskRepo taskRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public KpiProjectService(TaskRepo taskRepo, SimpMessagingTemplate messagingTemplate) {
        this.taskRepo = taskRepo;
        this.messagingTemplate = messagingTemplate;
    }

    public KpiProjectResponse calculateKpiForProject(Long projectId) {
        double onTimeCompletionRate = calculateOnTimeCompletionRate(projectId);
        List<KpiProjectResponse.TaskCycleTime> cycleTimes = findCycleTimesForCompletedTasks(projectId);
        double progress = calculateProgress(projectId);

        return new KpiProjectResponse(onTimeCompletionRate, cycleTimes, progress);
    }
    public double calculateOnTimeCompletionRate(Long projectId) {
        long totalCompletedTasks = taskRepo.countCompletedTasksByProjectId(projectId);
        if (totalCompletedTasks == 0) return 0;

        long onTimeTasks = taskRepo.countOnTimeTasksByProjectId(projectId);
        return (double) onTimeTasks / totalCompletedTasks * 100;
    }

    public List<KpiProjectResponse.TaskCycleTime> findCycleTimesForCompletedTasks(Long projectId) {
        List<Object[]> results = taskRepo.findTaskIdAndCycleTimeByProjectId(projectId);

        return results.stream()
                .map(row -> new KpiProjectResponse.TaskCycleTime(
                        row[0] != null ? ((Number) row[0]).longValue() : null,
                        row[1] != null ? ((Number) row[1]).longValue() : null
                ))
                .collect(Collectors.toList());
    }

    public double calculateProgress(Long projectId) {
        long totalTasks = taskRepo.countTasksByProjectId(projectId);
        if (totalTasks == 0) return 0;

        long completedTasks = taskRepo.countCompletedTasksByProjectId(projectId);
        return (double) completedTasks / totalTasks * 100;
    }
    public void recalculateKpiAndNotify(Long projectId) {
        KpiProjectResponse kpiResponse = calculateKpiForProject(projectId);
        messagingTemplate.convertAndSend("/topic/kpiProjectUpdates", kpiResponse);
    }

}
