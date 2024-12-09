package com.example.userkpi.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Map;
@Data
public class KpiResponse {
    @JsonFormat
    private double taskCompletionRate;
    @JsonFormat
    private double incompleteTaskRate;
    @JsonFormat
    private Map<String, Integer> taskStatusCount;
    //
    // Constructor
    public KpiResponse(double taskCompletionRate, double incompleteTaskRate, Map<String, Integer> taskStatusCount) {
        this.taskCompletionRate = taskCompletionRate;
        this.incompleteTaskRate = incompleteTaskRate;
        this.taskStatusCount = taskStatusCount;
    }

    @Override
    public String toString() {
        return "KpiResponse{" +
                "taskCompletionRate=" + taskCompletionRate +
                ", incompleteTaskRate=" + incompleteTaskRate +
                ", taskStatusCount=" + taskStatusCount +
                '}';
    }

}
