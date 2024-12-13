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

    // Get all notifications for the authenticated user
    @GetMapping("")
    @PreAuthorize("")
    public List<Notification> getNotifications() {
        return notificationService.getUserNotifications();
    }

    // Get the count of unseen notifications
    @GetMapping("/unseen-count")
    @PreAuthorize("")
    public Long getUnseenNotificationsCount() {
        return notificationService.countUnseenNotifications();
    }

    // Mark all notifications as read
    @GetMapping("/markAllAsRead")
    @PreAuthorize("")
    public void markAllAsRead() {
        notificationService.markAllNotificationsAsRead();
    }
}
