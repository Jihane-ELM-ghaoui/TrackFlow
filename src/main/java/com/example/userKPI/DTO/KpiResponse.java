package com.example.userKPI.DTO;

import java.util.Map;

public class KpiResponse {
    private double taskCompletionRate;
    private double incompleteTaskRate;
    private Map<String, Long> taskStatusCount;

    // Constructor
    public KpiResponse(double taskCompletionRate, double incompleteTaskRate, Map<String, Long> taskStatusCount) {
        this.taskCompletionRate = taskCompletionRate;
        this.incompleteTaskRate = incompleteTaskRate;
        this.taskStatusCount = taskStatusCount;
    }

    // Getters and Setters
    public double getTaskCompletionRate() {
        return taskCompletionRate;
    }

    public void setTaskCompletionRate(double taskCompletionRate) {
        this.taskCompletionRate = taskCompletionRate;
    }

    public double getIncompleteTaskRate() {
        return incompleteTaskRate;
    }

    public void setIncompleteTaskRate(double incompleteTaskRate) {
        this.incompleteTaskRate = incompleteTaskRate;
    }

    public Map<String, Long> getTaskStatusCount() {
        return taskStatusCount;
    }

    public void setTaskStatusCount(Map<String, Long> taskStatusCount) {
        this.taskStatusCount = taskStatusCount;
    }
}
