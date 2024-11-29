package com.example.userKPI.controller;

import com.example.userKPI.DTO.KpiProjectResponse;
import com.example.userKPI.service.KpiProjectService;
import com.example.userKPI.service.KpiService;
import com.example.userKPI.DTO.KpiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kpi")
@CrossOrigin(origins="http://localhost:3000")
public class KpiController {

    private final KpiService kpiService;
    private final KpiProjectService kpiProjectService;

    public KpiController(KpiService kpiService, KpiProjectService kpiProjectService) {
        this.kpiService = kpiService;
        this.kpiProjectService= kpiProjectService;
    }


    @GetMapping("/user")
    @PreAuthorize("")
    public ResponseEntity<KpiResponse> getKpisForUser() {
        KpiResponse kpiResponse = kpiService.calculateKpisForUser();
        return ResponseEntity.ok(kpiResponse);
    }
    @GetMapping("/project/{projectId}")
    public KpiProjectResponse getProjectKpi(@PathVariable Long projectId) {
        return kpiProjectService.calculateKpiForProject(projectId);
    }
}
