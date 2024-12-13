package com.example.devsecops.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data

@Entity
@Table(name = "message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String receiver;
    private String receivername;
    private String sendername;
    private String content;
    private LocalDateTime timestamp;

    public ChatMessage(String sender, String s, String s1, LocalDateTime now) {
    }
}

