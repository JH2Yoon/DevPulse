package com.jhy.devpulse.domain.log.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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
import com.jhy.devpulse.domain.log.dto.response.PageResponse;
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
    public ResponseEntity<PageResponse<LogResponse>> getLogs(
            Authentication authentication,
            @PathVariable("projectId") Long projectId,
            Pageable pageable) {

        Long memberId = (Long) authentication.getPrincipal();

        Page<LogResponse> logs = logService.getLogs(memberId, projectId, pageable);

        return ResponseEntity.ok(
                PageResponse.from(logs));
    }

    @GetMapping("/{logId}")
    public ResponseEntity<LogResponse> getLog(
            Authentication authentication,
            @PathVariable("logId") Long logId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                logService.getLog(memberId, logId));
    }
}