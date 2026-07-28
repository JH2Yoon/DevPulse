package com.jhy.devpulse.domain.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.project.dto.request.CreateProjectRequest;
import com.jhy.devpulse.domain.project.dto.request.UpdateProjectRequest;
import com.jhy.devpulse.domain.project.dto.response.ProjectResponse;
import com.jhy.devpulse.domain.project.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<Void> createProject(
            Authentication authentication,
            @Valid @RequestBody CreateProjectRequest request) {

        Long memberId = (Long) authentication.getPrincipal();

        projectService.createProject(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(
            Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(projectService.getProjects(memberId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                projectService.getProject(memberId, projectId));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(
            Authentication authentication,
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody UpdateProjectRequest request) {

        Long memberId = (Long) authentication.getPrincipal();

        projectService.updateProject(
                memberId,
                projectId,
                request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            Authentication authentication,
            @PathVariable("projectId") Long projectId) {

        Long memberId = (Long) authentication.getPrincipal();

        projectService.deleteProject(memberId, projectId);

        return ResponseEntity.noContent().build();
    }
}