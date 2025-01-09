package com.example.devsecops.service.impl;

import com.example.devsecops.model.Notification;
import com.example.devsecops.repository.NotificationRepo;
import com.example.devsecops.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public List<Notification> getUserNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        return notificationRepo.findByUserId(userId);
    }

    @Override
    public Long countUnseenNotifications() {
        return notificationRepo.countByIsReadFalse();
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead() {
        notificationRepo.markAllAsRead();
    }

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification = event.getNotification();

        notificationRepo.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications", notification);

    }

}
