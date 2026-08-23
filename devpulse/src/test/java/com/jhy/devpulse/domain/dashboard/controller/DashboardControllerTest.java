package com.jhy.devpulse.domain.dashboard.controller;

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

import com.jhy.devpulse.domain.dashboard.dto.response.DailyLogStatistics;
import com.jhy.devpulse.domain.dashboard.dto.response.DashboardResponse;
import com.jhy.devpulse.domain.dashboard.service.DashboardService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    @DisplayName("대시보드 조회 - 정상적으로 대시보드 정보를 반환한다")
    void getDashboard_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        DashboardResponse response = mock(DashboardResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(dashboardService.getDashboard(
                memberId,
                projectId
        )).thenReturn(response);

        // when
        ResponseEntity<DashboardResponse> result =
                dashboardController.getDashboard(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(dashboardService)
                .getDashboard(
                        memberId,
                        projectId
                );
    }

    @Test
    @DisplayName("주간 로그 조회 - 정상적으로 주간 로그 통계를 반환한다")
    void weeklyLogs_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        DailyLogStatistics statistics1 =
                mock(DailyLogStatistics.class);

        DailyLogStatistics statistics2 =
                mock(DailyLogStatistics.class);

        List<DailyLogStatistics> statistics =
                List.of(statistics1, statistics2);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(dashboardService.getWeeklyStatistics(
                memberId,
                projectId
        )).thenReturn(statistics);

        // when
        ResponseEntity<List<DailyLogStatistics>> result =
                dashboardController.weeklyLogs(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .hasSize(2)
                .isSameAs(statistics);

        verify(dashboardService)
                .getWeeklyStatistics(
                        memberId,
                        projectId
                );
    }
}