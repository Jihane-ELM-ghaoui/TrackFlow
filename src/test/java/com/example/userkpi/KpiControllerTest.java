package com.example.userkpi;

import com.example.userkpi.DTO.KpiProjectResponse;
import com.example.userkpi.DTO.KpiResponse;
import com.example.userkpi.controller.KpiController;
import com.example.userkpi.service.KpiProjectService;
import com.example.userkpi.service.KpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class KpiControllerTest {

    @InjectMocks
    private KpiController kpiController;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KpiService kpiService;

    @Mock
    private KpiProjectService kpiProjectService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        SecurityContextHolder.setContext(securityContext);
//    }

    @Test
    public void testGetUserTasks() {
        // Arrange
        Jwt jwt = new Jwt(
                "testToken",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", "testUser")
        );
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);

        String userId = "testUser";
        String url = String.format("http://task-service:8011/api/tasks/users?userId=%s", userId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("completedTasks", 5);
        responseBody.put("allTasks", 10);
        responseBody.put("tasksByStatus", Map.of("NOT_STARTED", 3, "IN_PROGRESS", 2, "COMPLETED", 5));
        responseBody.put("incompleteTasks", 5);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(ResponseEntity.ok(responseBody));

        KpiResponse kpiResponse = new KpiResponse(50.0, 50.0, Map.of("notStarted", 3, "inProgress", 2, "completed", 5));
        when(kpiService.calculateKpisForUser(10, 5, 5, Map.of("NOT_STARTED", 3, "IN_PROGRESS", 2, "COMPLETED", 5)))
                .thenReturn(kpiResponse);

        // Act
        ResponseEntity<KpiResponse> response = kpiController.getUserTasks();

        // Assert
        assertEquals(ResponseEntity.ok(kpiResponse), response);
    }

    @Test
    public void testGetProjectKpi() {
        // Arrange
        Long projectId = 1L;
        when(authentication.getName()).thenReturn("testUser");

        Jwt jwt = new Jwt(
                "testToken",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", "testUser")
        );
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);

        String url = String.format("http://task-service:8011/api/tasks/project?projectId=%s", projectId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tasksByProjectId", 20);
        responseBody.put("onTimeTasks", 15);
        responseBody.put("cycleTime", Map.of(1L, 5, 2L, 10));
        responseBody.put("completedTaskByProjectId", 18);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(ResponseEntity.ok(responseBody));

        KpiProjectResponse kpiProjectResponse = new KpiProjectResponse(75.0, List.of(
                new KpiProjectResponse.TaskCycleTime(1L, 5L),
                new KpiProjectResponse.TaskCycleTime(2L, 10L)
        ), 90.0);

        when(kpiProjectService.calculateKpisForProject(1L, Map.of(1L, 5, 2L, 10), 20L, 15L, 18L))
                .thenReturn(kpiProjectResponse);

        // Act
        KpiProjectResponse response = kpiController.getProjectKpi(projectId);

        // Assert
        assertEquals(kpiProjectResponse, response);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        // Ensure the mock is initialized correctly
        Jwt jwt = new Jwt(
                "testToken",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", "testUser")
        );
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);  // Mock authentication
    }


}
