package com.jhy.devpulse.domain.dashboard.service;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.dashboard.dto.response.DailyLogStatistics;
import com.jhy.devpulse.domain.dashboard.dto.response.DashboardResponse;
import com.jhy.devpulse.domain.log.entity.LogLevel;
import com.jhy.devpulse.domain.log.repository.LogRepository;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("대시보드 조회 - 정상적으로 통계 데이터를 조회한다")
    void getDashboard_success() {

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

        when(project.getName())
                .thenReturn("DevPulse");

        when(logRepository.countByProject(project))
                .thenReturn(100L);

        when(logRepository.countByProjectAndLevel(
                project,
                LogLevel.ERROR
        )).thenReturn(10L);

        when(logRepository.countByProjectAndLevel(
                project,
                LogLevel.WARN
        )).thenReturn(20L);

        when(logRepository.countByProjectAndLevel(
                project,
                LogLevel.INFO
        )).thenReturn(70L);

        when(logRepository.countByProjectAndCreatedAtAfter(
                eq(project),
                any(LocalDateTime.class)
        )).thenReturn(15L);

        // when
        DashboardResponse result =
                dashboardService.getDashboard(
                        memberId,
                        projectId
                );

        // then
        assertThat(result).isNotNull();

        assertThat(result.getProjectName())
                .isEqualTo("DevPulse");

        assertThat(result.getTotalLogs())
                .isEqualTo(100L);

        assertThat(result.getErrorLogs())
                .isEqualTo(10L);

        assertThat(result.getWarnLogs())
                .isEqualTo(20L);

        assertThat(result.getInfoLogs())
                .isEqualTo(70L);

        assertThat(result.getTodayLogs())
                .isEqualTo(15L);

        verify(memberRepository)
                .findById(memberId);

        verify(projectRepository)
                .findByIdAndMemberAndStatus(
                        projectId,
                        member,
                        ProjectStatus.ACTIVE
                );

        verify(logRepository)
                .countByProject(project);

        verify(logRepository)
                .countByProjectAndLevel(
                        project,
                        LogLevel.ERROR
                );

        verify(logRepository)
                .countByProjectAndLevel(
                        project,
                        LogLevel.WARN
                );

        verify(logRepository)
                .countByProjectAndLevel(
                        project,
                        LogLevel.INFO
                );

        verify(logRepository)
                .countByProjectAndCreatedAtAfter(
                        eq(project),
                        any(LocalDateTime.class)
                );
    }


    @Test
    @DisplayName("대시보드 조회 - 존재하지 않는 회원이면 예외가 발생한다")
    void getDashboard_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                dashboardService.getDashboard(
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

        verify(logRepository, never())
                .countByProject(any(Project.class));
    }


    @Test
    @DisplayName("대시보드 조회 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void getDashboard_projectNotFound() {

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
                dashboardService.getDashboard(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(logRepository, never())
                .countByProject(any(Project.class));
    }

    @Test
    @DisplayName("주간 통계 조회 - 최근 7일 로그 통계를 정상적으로 조회한다")
    void getWeeklyStatistics_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(project.getMember())
                .thenReturn(member);

        when(member.getId())
                .thenReturn(memberId);

        Object[] row1 = {
                Date.valueOf(LocalDate.of(2026, 8, 18)),
                10L
        };

        Object[] row2 = {
                Date.valueOf(LocalDate.of(2026, 8, 19)),
                20L
        };

        Object[] row3 = {
                Date.valueOf(LocalDate.of(2026, 8, 20)),
                30L
        };

        List<Object[]> weeklyResult =
                List.of(row1, row2, row3);

        when(logRepository.findWeeklyLogStatistics(
                eq(project),
                any(LocalDateTime.class)
        )).thenReturn(weeklyResult);

        // when
        List<DailyLogStatistics> result =
                dashboardService.getWeeklyStatistics(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .hasSize(3);

        assertThat(result.get(0).getDate())
                .isEqualTo(LocalDate.of(2026, 8, 18));

        assertThat(result.get(0).getCount())
                .isEqualTo(10L);

        assertThat(result.get(1).getDate())
                .isEqualTo(LocalDate.of(2026, 8, 19));

        assertThat(result.get(1).getCount())
                .isEqualTo(20L);

        assertThat(result.get(2).getDate())
                .isEqualTo(LocalDate.of(2026, 8, 20));

        assertThat(result.get(2).getCount())
                .isEqualTo(30L);

        verify(projectRepository)
                .findById(projectId);

        verify(logRepository)
                .findWeeklyLogStatistics(
                        eq(project),
                        any(LocalDateTime.class)
                );
    }


    @Test
    @DisplayName("주간 통계 조회 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void getWeeklyStatistics_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                dashboardService.getWeeklyStatistics(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(logRepository, never())
                .findWeeklyLogStatistics(
                        any(Project.class),
                        any(LocalDateTime.class)
                );
    }


    @Test
    @DisplayName("주간 통계 조회 - 다른 회원의 프로젝트라면 접근 거부 예외가 발생한다")
    void getWeeklyStatistics_accessDenied() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member owner = mock(Member.class);
        Project project = mock(Project.class);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(project.getMember())
                .thenReturn(owner);

        when(owner.getId())
                .thenReturn(999L);

        // when & then
        assertThatThrownBy(() ->
                dashboardService.getWeeklyStatistics(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.ACCESS_DENIED
                );

        verify(logRepository, never())
                .findWeeklyLogStatistics(
                        any(Project.class),
                        any(LocalDateTime.class)
                );
    }
}