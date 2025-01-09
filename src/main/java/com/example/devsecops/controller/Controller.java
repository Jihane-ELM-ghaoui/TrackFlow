package com.example.devsecops.controller;

import com.example.devsecops.model.Notification;
import com.example.devsecops.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin("http://localhost:3000")
@RequestMapping("/notification")
public class Controller {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    @PreAuthorize("")
    public List<Notification> getNotifications() {
        return notificationService.getUserNotifications();
    }

    @GetMapping("/unseen-count")
    @PreAuthorize("")
    public Long getUnseenNotificationsCount() {
        return notificationService.countUnseenNotifications();
    }

    @GetMapping("/markAllAsRead")
    @PreAuthorize("")
    public void markAllAsRead() {
        notificationService.markAllNotificationsAsRead();
    }
}
