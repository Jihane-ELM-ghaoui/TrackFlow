package com.example.userKPI;

import com.example.userKPI.DTO.KpiResponse;
import com.example.userKPI.repo.TaskRepo;
import com.example.userKPI.service.KpiService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiServiceTest {

    private final TaskRepo taskRepo = mock(TaskRepo.class);
    private final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);

    // No need to mock `KpiService` here since it’s used directly in the first test
    private final KpiService service = new KpiService(taskRepo, messagingTemplate);

    @Test
    void testCalculateKpisForUser() {
        // Mock security context
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("user1");
        SecurityContextHolder.setContext(securityContext);

        // Mock repository calls
        when(taskRepo.countAllTasksByUserId("user1")).thenReturn(10);
        when(taskRepo.countCompletedTasksByUserId("user1")).thenReturn(6);
        when(taskRepo.countIncompleteTasksByUserId("user1")).thenReturn(4);

        List<Object[]> mockStatusCounts = Arrays.asList(
                new Object[]{"NOT_STARTED", 3L},
                new Object[]{"IN_PROGRESS", 1L},
                new Object[]{"COMPLETED", 6L}
        );
        when(taskRepo.countTasksByStatus("user1")).thenReturn(mockStatusCounts);

        // Call the method under test
        KpiResponse response = service.calculateKpisForUser();

        // Verify the response
        assertEquals(60.0, response.getTaskCompletionRate(), 0.01);
        assertEquals(40.0, response.getIncompleteTaskRate(), 0.01);
        assertEquals(3L, response.getTaskStatusCount().get("notStarted"));
        assertEquals(1L, response.getTaskStatusCount().get("inProgress"));
    }

    @Test
    void testRecalculateKpiForUserAndNotify() {
        // Create a spy for `KpiService` to allow partial mocking
        KpiService serviceSpy = spy(new KpiService(taskRepo, messagingTemplate));

        // Stub the `calculateKpisForUser` method
        KpiResponse mockResponse = new KpiResponse(80.0, 20.0, new HashMap<>());
        doReturn(mockResponse).when(serviceSpy).calculateKpisForUser();

        // Call the method under test
        serviceSpy.recalculateKpiForUserAndNotify();

        // Verify that the messaging template sends the correct message
        verify(messagingTemplate).convertAndSend("/topic/kpiUpdates", mockResponse);
    }
}
