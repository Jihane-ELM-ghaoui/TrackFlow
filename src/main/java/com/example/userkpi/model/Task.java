package com.example.userkpi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String taskName;
    private String taskDescription;
    private int taskPriority;
    private Long projectId;
    private String assignedUser;
    private Date taskStartDate;
    private Date taskEstimatedEndDate;
    private Date taskEndDate;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
