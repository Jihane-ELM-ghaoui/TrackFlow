package com.example.userKPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskid;


    private String assignedUser;
    private String taskname;
    private Date startDate;
    private Date completionDate;
    private Date estimatedFinishDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
