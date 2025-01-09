package com.example.devsecops;

import com.example.devsecops.model.Notification;
import com.example.devsecops.repository.NotificationRepo;
import com.example.devsecops.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepo notificationRepo;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }

    @Test
    void testGetUserNotifications() {
        String userId = "auth0|user123";
        when(jwtAuthenticationToken.getName()).thenReturn(userId);

        List<Notification> mockNotifications = List.of(new Notification(), new Notification());
        when(notificationRepo.findByUserId(userId)).thenReturn(mockNotifications);

        List<Notification> result = notificationService.getUserNotifications();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationRepo).findByUserId(userId);
    }

    @Test
    void testCountUnseenNotifications() {
        when(notificationRepo.countByIsReadFalse()).thenReturn(5L);

        Long result = notificationService.countUnseenNotifications();

        assertNotNull(result);
        assertEquals(5, result);
        verify(notificationRepo).countByIsReadFalse();
    }

    @Test
    void testMarkAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead();

        verify(notificationRepo).markAllAsRead();
    }
}
