package com.jhy.devpulse.domain.alert.controller;

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

import com.jhy.devpulse.domain.alert.dto.response.AlertResponse;
import com.jhy.devpulse.domain.alert.service.AlertService;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AlertController alertController;


    @Test
    @DisplayName("알림 목록 조회 - 정상적으로 알림 목록을 반환한다")
    void getAlerts_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        AlertResponse alert1 = mock(AlertResponse.class);
        AlertResponse alert2 = mock(AlertResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(alertService.getAlerts(
                memberId,
                projectId
        )).thenReturn(List.of(alert1, alert2));

        // when
        ResponseEntity<List<AlertResponse>> response =
                alertController.getAlerts(
                        authentication,
                        projectId
                );

        // then
        assertThat(response.getStatusCode().value())
                .isEqualTo(200);

        assertThat(response.getBody())
                .hasSize(2);

        verify(alertService)
                .getAlerts(memberId, projectId);
    }


    @Test
    @DisplayName("알림 읽음 처리 - 정상적으로 읽음 처리한다")
    void readAlert_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        // when
        ResponseEntity<Void> response =
                alertController.readAlert(
                        authentication,
                        projectId,
                        alertId
                );

        // then
        assertThat(response.getStatusCode().value())
                .isEqualTo(204);

        verify(alertService)
                .readAlert(
                        memberId,
                        projectId,
                        alertId
                );
    }


    @Test
    @DisplayName("알림 삭제 - 정상적으로 알림을 삭제한다")
    void deleteAlert_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        // when
        ResponseEntity<Void> response =
                alertController.deleteAlert(
                        authentication,
                        projectId,
                        alertId
                );

        // then
        assertThat(response.getStatusCode().value())
                .isEqualTo(204);

        verify(alertService)
                .deleteAlert(
                        memberId,
                        projectId,
                        alertId
                );
    }
}