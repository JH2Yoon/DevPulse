package com.jhy.devpulse.domain.log.service;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.alert.service.AlertService;
import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.apikey.entity.ApiKeyStatus;
import com.jhy.devpulse.domain.apikey.repository.ApiKeyRepository;
import com.jhy.devpulse.domain.log.dto.request.CreateLogRequest;
import com.jhy.devpulse.domain.log.dto.request.LogSearchCondition;
import com.jhy.devpulse.domain.log.dto.response.LogResponse;
import com.jhy.devpulse.domain.log.entity.*;
import com.jhy.devpulse.domain.log.repository.*;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.*;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LogDocumentRepository logDocumentRepository;

    @Mock
    private LogSearchRepository logSearchRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private LogService logService;

    @Test
    @DisplayName("로그 생성 - 정상적으로 로그를 저장한다")
    void createLog_success() {

        // given
        String apiKey = "dp_live_test";
        CreateLogRequest request = mock(CreateLogRequest.class);

        ApiKey key = mock(ApiKey.class);
        Project project = mock(Project.class);
        Log log = mock(Log.class);

        when(request.getLevel()).thenReturn(LogLevel.INFO);
        when(request.getServiceName()).thenReturn("devpulse");
        when(request.getMessage()).thenReturn("Application started");
        when(request.getStackTrace()).thenReturn(null);

        when(key.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(1L);

        when(apiKeyRepository.findByApiKeyAndStatus(
                apiKey,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(key));

        when(projectRepository.findByIdAndStatus(
                1L,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(logRepository.save(any(Log.class))).thenReturn(log);
        when(log.getLevel()).thenReturn(LogLevel.INFO);
        when(log.getProject()).thenReturn(project);

        // when
        logService.createLog(apiKey, request);

        // then
        verify(apiKeyRepository).findByApiKeyAndStatus(
                apiKey,
                ApiKeyStatus.ACTIVE
        );

        verify(projectRepository).findByIdAndStatus(
                1L,
                ProjectStatus.ACTIVE
        );

        verify(logRepository).save(any(Log.class));
        verify(logDocumentRepository).save(any(LogDocument.class));

        verify(alertService, never()).createAlert(
                any(Project.class),
                any(Log.class)
        );
    }

    @Test
    @DisplayName("로그 생성 - 존재하지 않는 API Key라면 예외가 발생한다")
    void createLog_apiKeyNotFound() {

        // given
        String apiKey = "invalid-key";
        CreateLogRequest request = mock(CreateLogRequest.class);

        when(apiKeyRepository.findByApiKeyAndStatus(
                apiKey,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.createLog(apiKey, request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.API_KEY_NOT_FOUND
                );

        verify(logRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그 생성 - 존재하지 않는 프로젝트라면 예외가 발생한다")
    void createLog_projectNotFound() {

        // given
        String apiKey = "dp_live_test";
        CreateLogRequest request = mock(CreateLogRequest.class);

        ApiKey key = mock(ApiKey.class);
        Project project = mock(Project.class);

        when(key.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(1L);

        when(apiKeyRepository.findByApiKeyAndStatus(
                apiKey,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(key));

        when(projectRepository.findByIdAndStatus(
                1L,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.createLog(apiKey, request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(logRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그 생성 - ERROR 로그라면 Alert를 생성한다")
    void createLog_errorLog_createsAlert() {

        // given
        String apiKey = "dp_live_test";
        CreateLogRequest request = mock(CreateLogRequest.class);

        ApiKey key = mock(ApiKey.class);
        Project project = mock(Project.class);
        Log log = mock(Log.class);

        when(request.getLevel()).thenReturn(LogLevel.ERROR);
        when(request.getServiceName()).thenReturn("devpulse");
        when(request.getMessage()).thenReturn("Database connection failed");
        when(request.getStackTrace()).thenReturn("Connection refused");

        when(key.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(1L);

        when(apiKeyRepository.findByApiKeyAndStatus(
                apiKey,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(key));

        when(projectRepository.findByIdAndStatus(
                1L,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(logRepository.save(any(Log.class))).thenReturn(log);
        when(log.getLevel()).thenReturn(LogLevel.ERROR);
        when(log.getProject()).thenReturn(project);

        // when
        logService.createLog(apiKey, request);

        // then
        verify(logRepository).save(any(Log.class));
        verify(logDocumentRepository).save(any(LogDocument.class));

        verify(alertService).createAlert(
                project,
                log
        );
    }

    @Test
    @DisplayName("로그 조회 - 프로젝트의 로그를 페이징하여 조회한다")
    void getLogs_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        LogSearchCondition condition = new LogSearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        LogDocument logDocument = mock(LogDocument.class);

        Page<LogDocument> logPage =
                new PageImpl<>(java.util.List.of(logDocument), pageable, 1);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(project.getId()).thenReturn(projectId);

        when(logSearchRepository.search(
                projectId,
                condition,
                pageable
        )).thenReturn(logPage);

        // when
        Page<LogResponse> result =
                logService.getLogs(
                        memberId,
                        projectId,
                        condition,
                        pageable
                );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(memberRepository).findById(memberId);

        verify(projectRepository).findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        );

        verify(logSearchRepository).search(
                projectId,
                condition,
                pageable
        );
    }

    @Test
    @DisplayName("로그 조회 - 존재하지 않는 회원이라면 예외가 발생한다")
    void getLogs_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        LogSearchCondition condition = new LogSearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.getLogs(
                        memberId,
                        projectId,
                        condition,
                        pageable
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(logSearchRepository, never())
                .search(anyLong(), any(), any());
    }

    @Test
    @DisplayName("로그 조회 - 접근 가능한 프로젝트가 아니라면 예외가 발생한다")
    void getLogs_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        LogSearchCondition condition = new LogSearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.getLogs(
                        memberId,
                        projectId,
                        condition,
                        pageable
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(logSearchRepository, never())
                .search(anyLong(), any(), any());
    }

    @Test
    @DisplayName("로그 상세 조회 - 정상적으로 로그를 조회한다")
    void getLog_success() {

        // given
        Long memberId = 1L;
        Long logId = 100L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        Log log = mock(Log.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(logRepository.findById(logId))
                .thenReturn(Optional.of(log));

        when(log.getProject()).thenReturn(project);
        when(project.getMember()).thenReturn(member);

        // when
        LogResponse result =
                logService.getLog(memberId, logId);

        // then
        assertThat(result).isNotNull();

        verify(memberRepository).findById(memberId);
        verify(logRepository).findById(logId);
    }

    @Test
    @DisplayName("로그 상세 조회 - 존재하지 않는 회원이라면 예외가 발생한다")
    void getLog_memberNotFound() {

        // given
        Long memberId = 1L;
        Long logId = 100L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.getLog(memberId, logId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(logRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("로그 상세 조회 - 존재하지 않는 로그라면 예외가 발생한다")
    void getLog_logNotFound() {

        // given
        Long memberId = 1L;
        Long logId = 100L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(logRepository.findById(logId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                logService.getLog(memberId, logId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.LOG_NOT_FOUND
                );
    }

    @Test
    @DisplayName("로그 상세 조회 - 다른 회원의 로그라면 접근 거부 예외가 발생한다")
    void getLog_accessDenied() {

        // given
        Long memberId = 1L;
        Long logId = 100L;

        Member member = mock(Member.class);
        Member otherMember = mock(Member.class);
        Project project = mock(Project.class);
        Log log = mock(Log.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(logRepository.findById(logId))
                .thenReturn(Optional.of(log));

        when(log.getProject()).thenReturn(project);
        when(project.getMember()).thenReturn(otherMember);

        // when & then
        assertThatThrownBy(() ->
                logService.getLog(memberId, logId)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.ACCESS_DENIED
                );
    }
}