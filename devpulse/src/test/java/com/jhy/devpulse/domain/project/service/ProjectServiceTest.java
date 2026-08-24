package com.jhy.devpulse.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.dto.request.CreateProjectRequest;
import com.jhy.devpulse.domain.project.dto.request.UpdateProjectRequest;
import com.jhy.devpulse.domain.project.dto.response.ProjectResponse;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("프로젝트 생성 - 정상적으로 프로젝트를 생성한다")
    void createProject_success() {

        // given
        Long memberId = 1L;

        Member member = mock(Member.class);

        CreateProjectRequest request = mock(CreateProjectRequest.class);

        when(request.getName()).thenReturn("DevPulse");
        when(request.getDescription()).thenReturn("DevOps SaaS Project");

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        // when
        projectService.createProject(memberId, request);

        // then
        verify(memberRepository).findById(memberId);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 생성 - 존재하지 않는 회원이면 예외가 발생한다")
    void createProject_memberNotFound() {

        // given
        Long memberId = 1L;

        CreateProjectRequest request = mock(CreateProjectRequest.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.createProject(memberId, request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 목록 조회 - 회원의 활성 프로젝트를 조회한다")
    void getProjects_success() {

        // given
        Long memberId = 1L;

        Member member = mock(Member.class);

        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findAllByMemberAndStatus(
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(List.of(project1, project2));

        // when
        List<ProjectResponse> result =
                projectService.getProjects(memberId);

        // then
        assertThat(result).hasSize(2);

        verify(memberRepository).findById(memberId);

        verify(projectRepository).findAllByMemberAndStatus(
                member,
                ProjectStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("프로젝트 목록 조회 - 존재하지 않는 회원이면 예외가 발생한다")
    void getProjects_memberNotFound() {

        // given
        Long memberId = 1L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.getProjects(memberId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findAllByMemberAndStatus(any(), any());
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 정상적으로 프로젝트를 조회한다")
    void getProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        // when
        ProjectResponse result =
                projectService.getProject(memberId, projectId);

        // then
        assertThat(result).isNotNull();

        verify(memberRepository).findById(memberId);

        verify(projectRepository).findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 존재하지 않는 회원이면 예외가 발생한다")
    void getProject_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.getProject(memberId, projectId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void getProject_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.getProject(memberId, projectId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );
    }

    @Test
    @DisplayName("프로젝트 수정 - 정상적으로 프로젝트를 수정한다")
    void updateProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        UpdateProjectRequest request = mock(UpdateProjectRequest.class);

        when(request.getName()).thenReturn("Updated DevPulse");
        when(request.getDescription())
                .thenReturn("Updated Description");

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        // when
        projectService.updateProject(
                memberId,
                projectId,
                request
        );

        // then
        verify(memberRepository).findById(memberId);

        verify(projectRepository).findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        );

        verify(project).update(
                "Updated DevPulse",
                "Updated Description"
        );
    }

    @Test
    @DisplayName("프로젝트 수정 - 존재하지 않는 회원이면 예외가 발생한다")
    void updateProject_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        UpdateProjectRequest request =
                mock(UpdateProjectRequest.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.updateProject(
                        memberId,
                        projectId,
                        request
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName("프로젝트 수정 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void updateProject_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        UpdateProjectRequest request =
                mock(UpdateProjectRequest.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.updateProject(
                        memberId,
                        projectId,
                        request
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );
    }

    @Test
    @DisplayName("프로젝트 삭제 - 프로젝트를 비활성화한다")
    void deleteProject_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        // when
        projectService.deleteProject(memberId, projectId);

        // then
        verify(memberRepository).findById(memberId);

        verify(projectRepository).findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        );

        verify(project).deactivate();
    }

    @Test
    @DisplayName("프로젝트 삭제 - 존재하지 않는 회원이면 예외가 발생한다")
    void deleteProject_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.deleteProject(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName("프로젝트 삭제 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void deleteProject_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                projectService.deleteProject(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(projectRepository, never()).delete(any());
    }
}