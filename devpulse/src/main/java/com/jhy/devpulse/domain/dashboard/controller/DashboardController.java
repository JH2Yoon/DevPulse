package com.jhy.devpulse.domain.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.dashboard.dto.response.DailyLogStatistics;
import com.jhy.devpulse.domain.dashboard.dto.response.DashboardResponse;
import com.jhy.devpulse.domain.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getDashboard(memberId, projectId));
    }

    @GetMapping("/projects/{projectId}/logs/weekly")
    public ResponseEntity<List<DailyLogStatistics>> weeklyLogs(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getWeeklyStatistics(
                        memberId,
                        projectId));
    }
}