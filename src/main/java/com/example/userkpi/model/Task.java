package com.example.userkpi.model;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


@Data
public class Task {
    @JsonProperty("taskName")
    private String taskName;

    @JsonProperty("taskDescription")
    private String taskDescription;

    @JsonProperty("taskPriority")
    private int taskPriority;

    @JsonProperty("projectId")
    private int projectId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("taskStartDate")
    private Date taskStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("taskEstimatedEndDate")
    private Date taskEstimatedEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("taskEndDate")
    private Date taskEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("status")
    private String status;



    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskPriority=" + taskPriority +
                ", projectId=" + projectId +
                ", taskStartDate=" + taskStartDate +
                ", taskEstimatedEndDate=" + taskEstimatedEndDate +
                ", taskEndDate=" + taskEndDate +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }
}
