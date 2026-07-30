package com.jhy.devpulse.domain.log.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.log.dto.request.CreateLogRequest;
import com.jhy.devpulse.domain.log.dto.response.LogResponse;
import com.jhy.devpulse.domain.log.service.LogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<Void> createLog(
            @RequestHeader("X-API-KEY") String apiKey,
            @Valid @RequestBody CreateLogRequest request) {

        logService.createLog(apiKey, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<LogResponse>> getLogs(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                logService.getLogs(memberId, projectId));
    }
}