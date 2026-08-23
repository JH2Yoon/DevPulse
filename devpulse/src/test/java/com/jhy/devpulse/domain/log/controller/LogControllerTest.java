package com.jhy.devpulse.domain.log.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.jhy.devpulse.domain.log.dto.request.CreateLogRequest;
import com.jhy.devpulse.domain.log.dto.request.LogSearchCondition;
import com.jhy.devpulse.domain.log.dto.response.LogResponse;
import com.jhy.devpulse.domain.log.dto.response.PageResponse;
import com.jhy.devpulse.domain.log.service.LogService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogController logController;

    @Test
    @DisplayName("로그 생성 - 정상적으로 로그를 생성한다")
    void createLog_success() {

        // given
        String apiKey = "dp_live_test123";

        CreateLogRequest request =
                mock(CreateLogRequest.class);

        // when
        ResponseEntity<Void> result =
                logController.createLog(
                        apiKey,
                        request
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(201);

        assertThat(result.getBody())
                .isNull();

        verify(logService)
                .createLog(
                        apiKey,
                        request
                );
    }

    @Test
    @DisplayName("로그 목록 조회 - 정상적으로 로그 목록을 반환한다")
    void getLogs_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        LogSearchCondition condition =
                mock(LogSearchCondition.class);

        Pageable pageable =
                PageRequest.of(0, 20);

        LogResponse log1 =
                mock(LogResponse.class);

        LogResponse log2 =
                mock(LogResponse.class);

        Page<LogResponse> logPage =
                new PageImpl<>(
                        List.of(log1, log2),
                        pageable,
                        2
                );

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(logService.getLogs(
                memberId,
                projectId,
                condition,
                pageable
        )).thenReturn(logPage);

        // when
        ResponseEntity<PageResponse<LogResponse>> result =
                logController.getLogs(
                        authentication,
                        projectId,
                        condition,
                        pageable
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isNotNull();

        verify(logService)
                .getLogs(
                        memberId,
                        projectId,
                        condition,
                        pageable
                );
    }

    @Test
    @DisplayName("로그 상세 조회 - 정상적으로 로그를 조회한다")
    void getLog_success() {

        // given
        Long memberId = 1L;
        Long logId = 100L;

        LogResponse response =
                mock(LogResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(logService.getLog(
                memberId,
                logId
        )).thenReturn(response);

        // when
        ResponseEntity<LogResponse> result =
                logController.getLog(
                        authentication,
                        logId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(logService)
                .getLog(
                        memberId,
                        logId
                );
    }
}