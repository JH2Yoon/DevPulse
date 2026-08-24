package com.jhy.devpulse.domain.alert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.alert.dto.response.AlertResponse;
import com.jhy.devpulse.domain.alert.service.AlertService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                alertService.getAlerts(memberId, projectId));
    }

    @PatchMapping("/{alertId}/read")
    public ResponseEntity<Void> readAlert(
            Authentication authentication,
            @PathVariable("projectId") Long projectId,
            @PathVariable("alertId") Long alertId) {

        Long memberId = (Long) authentication.getPrincipal();

        alertService.readAlert(memberId, projectId, alertId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(
            Authentication authentication,
            @PathVariable("projectId") Long projectId,
            @PathVariable("alertId") Long alertId) {

        Long memberId = (Long) authentication.getPrincipal();

        alertService.deleteAlert(memberId, projectId, alertId);

        return ResponseEntity.noContent().build();
    }
}