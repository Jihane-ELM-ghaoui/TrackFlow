package com.example.userkpi.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;
@Data

public class KpiProjectResponse {

    @JsonFormat
    private double onTimeCompletionRate;
    @JsonFormat
    private List<TaskCycleTime> cycleTimes;
    @JsonFormat
    private double progress;

    // Constructor with values without units
    public KpiProjectResponse(double onTimeCompletionRate, List<TaskCycleTime> cycleTimes, double progress) {
        this.onTimeCompletionRate = onTimeCompletionRate;
        this.cycleTimes = cycleTimes;
        this.progress = progress;

    }

    @Data
    public static class TaskCycleTime {

        @JsonFormat
        private Long taskId;
        @JsonFormat
        private Long cycleTime;

        public TaskCycleTime(Long taskId, Long cycleTime) {
            this.taskId = taskId;
            this.cycleTime = cycleTime;
        }

        @Override
        public String toString() {
            return "TaskCycleTime{" +
                    "taskId=" + taskId +
                    ", cycleTime=" + cycleTime +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "KpiProjectResponse{" +
                "onTimeCompletionRate=" + onTimeCompletionRate +
                ", cycleTimes=" + cycleTimes +
                ", progress=" + progress +
                '}';
    }
}
