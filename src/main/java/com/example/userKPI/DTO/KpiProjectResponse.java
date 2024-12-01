package com.example.userKPI.DTO;

import lombok.Data;

import java.util.List;
@Data


public class KpiProjectResponse {
    private double onTimeCompletionRate; // Percentage value without the '%' symbol
    private List<TaskCycleTime> cycleTimes; // List of cycle times for each completed task
    private double progress; // Percentage value without the '%' symbol

    // Constructor with values without units
    public KpiProjectResponse(double onTimeCompletionRate, List<TaskCycleTime> cycleTimes, double progress) {
        this.onTimeCompletionRate = onTimeCompletionRate; // Store raw value
        this.cycleTimes = cycleTimes; // List of cycle times in days
        this.progress = progress; // Store raw value

    }
    public static class TaskCycleTime {
        private Long taskId;
        private Long cycleTime;

        public TaskCycleTime(Long taskId, Long cycleTime) {
            this.taskId = taskId;
            this.cycleTime = cycleTime;
        }

        public Long getTaskId() {
            return taskId;
        }

        public Long getCycleTime() {
            return cycleTime;
        }
    }
}
