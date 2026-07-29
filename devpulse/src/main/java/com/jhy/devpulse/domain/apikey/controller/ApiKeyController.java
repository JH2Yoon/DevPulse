package com.jhy.devpulse.domain.apikey.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.apikey.dto.response.ApiKeyResponse;
import com.jhy.devpulse.domain.apikey.service.ApiKeyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects/{projectId}/apikey")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    /**
     * API Key 발급
     */
    @PostMapping
    public ResponseEntity<ApiKeyResponse> createApiKey(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        ApiKeyResponse response = apiKeyService.createApiKey(memberId, projectId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * API Key 조회
     */
    @GetMapping
    public ResponseEntity<ApiKeyResponse> getApiKey(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                apiKeyService.getApiKey(memberId, projectId));
    }

    /**
     * API Key 재발급
     */
    @PatchMapping
    public ResponseEntity<ApiKeyResponse> regenerateApiKey(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                apiKeyService.regenerateApiKey(memberId, projectId));
    }
}
