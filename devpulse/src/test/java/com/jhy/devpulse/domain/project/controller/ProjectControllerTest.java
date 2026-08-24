package com.jhy.devpulse.domain.project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.jhy.devpulse.domain.project.dto.request.CreateProjectRequest;
import com.jhy.devpulse.domain.project.dto.request.UpdateProjectRequest;
import com.jhy.devpulse.domain.project.dto.response.ProjectResponse;
import com.jhy.devpulse.domain.project.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectController projectController;


    @Test
    @DisplayName("프로젝트 생성 - 정상적으로 프로젝트를 생성한다")
    void createProject_success() {

        // given
        Long memberId = 1L;

        CreateProjectRequest request =
                mock(CreateProjectRequest.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        // when
        ResponseEntity<Void> result =
                projectController.createProject(
                        authentication,
                        request
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(201);

        assertThat(result.getBody())
                .isNull();

        verify(projectService)
                .createProject(
                        memberId,
                        request
                );
    }

    @Test
    @DisplayName("프로젝트 목록 조회 - 정상적으로 프로젝트 목록을 반환한다")
    void getProjects_success() {

        // given
        Long memberId = 1L;

        ProjectResponse project1 =
                mock(ProjectResponse.class);

        ProjectResponse project2 =
                mock(ProjectResponse.class);

        List<ProjectResponse> projects =
                List.of(project1, project2);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(projectService.getProjects(memberId))
                .thenReturn(projects);

        // when
        ResponseEntity<List<ProjectResponse>> result =
                projectController.getProjects(authentication);

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .hasSize(2)
                .isSameAs(projects);

        verify(projectService)
                .getProjects(memberId);
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 정상적으로 프로젝트를 반환한다")
    void getProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        ProjectResponse response =
                mock(ProjectResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(projectService.getProject(
                memberId,
                projectId
        )).thenReturn(response);

        // when
        ResponseEntity<ProjectResponse> result =
                projectController.getProject(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(projectService)
                .getProject(
                        memberId,
                        projectId
                );
    }

    @Test
    @DisplayName("프로젝트 수정 - 정상적으로 프로젝트를 수정한다")
    void updateProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        UpdateProjectRequest request =
                mock(UpdateProjectRequest.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        // when
        ResponseEntity<Void> result =
                projectController.updateProject(
                        authentication,
                        projectId,
                        request
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isNull();

        verify(projectService)
                .updateProject(
                        memberId,
                        projectId,
                        request
                );
    }

    @Test
    @DisplayName("프로젝트 삭제 - 정상적으로 프로젝트를 삭제한다")
    void deleteProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        // when
        ResponseEntity<Void> result =
                projectController.deleteProject(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(204);

        assertThat(result.getBody())
                .isNull();

        verify(projectService)
                .deleteProject(
                        memberId,
                        projectId
                );
    }
}