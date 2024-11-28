package com.example.userKPI.controller;

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

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GetMapping("/user")
    @PreAuthorize("")
    public ResponseEntity<KpiResponse> getKpisForUser() {
        KpiResponse kpiResponse = kpiService.calculateKpisForUser();
        return ResponseEntity.ok(kpiResponse);
    }
}
