package com.example.userkpi.controller;

import com.example.userkpi.DTO.KpiProjectResponse;
import com.example.userkpi.DTO.KpiResponse;
import com.example.userkpi.service.KpiProjectService;
import com.example.userkpi.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

//@CrossOrigin(origins="http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/kpi")
public class KpiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KpiService kpiService;

    @Autowired
    private KpiProjectService kpiProjectService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Service method to fetch user tasks and calculate KPIs
    @GetMapping("/user")
    public ResponseEntity<KpiResponse> getUserTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        String url = String.format("http://task-service:8011/api/tasks/users?userId=%s", userId);

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        String token = jwtToken.getToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> responseBody = response.getBody();

        Integer completedTasks = (Integer) responseBody.get("completedTasks");
        Integer totalTasks = (Integer) responseBody.get("allTasks");
        Map<String, Integer> tasksByStatus = (Map<String, Integer>) responseBody.get("tasksByStatus");
        Integer incompleteTasks = (Integer) responseBody.get("incompleteTasks");

        KpiResponse kpiResponse = kpiService.calculateKpisForUser(totalTasks, completedTasks, incompleteTasks, tasksByStatus);

        return ResponseEntity.ok(kpiResponse);
    }

    // Service method to get project KPIs
    @GetMapping("/project/{projectId}")
    public KpiProjectResponse getProjectKpi(@PathVariable Long projectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String url = String.format("http://task-service:8011/api/tasks/project?projectId=%s", projectId);

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        String token = jwtToken.getToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> responseBody = response.getBody();

        Long totalTasksByProjectId = ((Integer) responseBody.get("tasksByProjectId")).longValue();
        Long onTimeTasksByProjectId = ((Integer) responseBody.get("onTimeTasks")).longValue();
        Map<Long, Integer> cycleTime = (Map<Long, Integer>) responseBody.get("cycleTime");
        Long completedTaskByProjectId = ((Integer) responseBody.get("completedTaskByProjectId")).longValue();

        KpiProjectResponse kpiProjectResponse = kpiProjectService.calculateKpisForProject(projectId, cycleTime, totalTasksByProjectId, onTimeTasksByProjectId, completedTaskByProjectId);
        // Publish to WebSocket topic
        messagingTemplate.convertAndSend("/topic/kpiProjectUpdates", kpiProjectResponse);

        return kpiProjectResponse;
    }

    @MessageMapping("/task/update")  // Listen for task update
    public void handleTaskUpdate(Map<String, Long> payload) {
        Long taskId = payload.get("taskId");
        Long projectId = payload.get("projectId");

        // Trigger KPI update for the project and user
        KpiProjectResponse updatedKpiData = recalculateKpiForTask(projectId);
        messagingTemplate.convertAndSend("/topic/kpiProjectUpdates", updatedKpiData);
    }

    public KpiProjectResponse recalculateKpiForTask(Long projectId) {
        // Recalculate project KPIs
        return getProjectKpi(projectId);
    }
}
