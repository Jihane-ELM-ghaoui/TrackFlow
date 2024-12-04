package com.example.userkpi.DTO;

import lombok.Data;

import java.util.List;
@Data

public class KpiProjectResponse {

    private double onTimeCompletionRate;
    private List<TaskCycleTime> cycleTimes;
    private double progress;

    // Constructor with values without units
    public KpiProjectResponse(double onTimeCompletionRate, List<TaskCycleTime> cycleTimes, double progress) {
        this.onTimeCompletionRate = onTimeCompletionRate;
        this.cycleTimes = cycleTimes;
        this.progress = progress;

    }
    @Data
    public static class TaskCycleTime {
        private Long taskId;
        private Long cycleTime;

        public TaskCycleTime(Long taskId, Long cycleTime) {
            this.taskId = taskId;
            this.cycleTime = cycleTime;
        }
//
//        public Long getTaskId() {
//            return taskId;
//        }
//
//        public Long getCycleTime() {
//            return cycleTime;
//        }
    }
}
