package com.example.userKPI.service;

import com.example.userKPI.DTO.KpiProjectResponse;
import com.example.userKPI.repo.TaskRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiProjectService {
    private final TaskRepo taskRepo;

    public KpiProjectService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public KpiProjectResponse calculateKpiForProject(Long projectId) {
        double onTimeCompletionRate = calculateOnTimeCompletionRate(projectId);
        List<Long> cycleTimes = findCycleTimesForCompletedTasks(projectId);
        double progress = calculateProgress(projectId);

        return new KpiProjectResponse(onTimeCompletionRate, cycleTimes, progress);
    }
    public double calculateOnTimeCompletionRate(Long projectId) {
        long totalCompletedTasks = taskRepo.countCompletedTasksByProjectId(projectId);
        if (totalCompletedTasks == 0) return 0;

        long onTimeTasks = taskRepo.countOnTimeTasksByProjectId(projectId);
        return (double) onTimeTasks / totalCompletedTasks * 100;
    }

    public List<Long> findCycleTimesForCompletedTasks(Long projectId) {
        return taskRepo.findCycleTimesForCompletedTasksByProjectId(projectId);
    }

    public double calculateProgress(Long projectId) {
        long totalTasks = taskRepo.countTasksByProjectId(projectId);
        if (totalTasks == 0) return 0;

        long completedTasks = taskRepo.countCompletedTasksByProjectId(projectId);
        return (double) completedTasks / totalTasks * 100;
    }

}
