package com.example.devsecops.service;

import com.example.devsecops.model.Notification;
import jakarta.transaction.Transactional;

import java.util.List;

public interface NotificationService {

    List<Notification> getUserNotifications();

    Long countUnseenNotifications();

    @Transactional
    void markAllNotificationsAsRead();
}
