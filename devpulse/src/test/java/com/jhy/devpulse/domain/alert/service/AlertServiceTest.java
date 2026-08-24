package com.jhy.devpulse.domain.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.jhy.devpulse.domain.alert.dto.response.AlertResponse;
import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.alert.repository.AlertRepository;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.notification.service.NotificationService;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AlertService alertService;


    @Test
    @DisplayName("알림 생성 - 정상적으로 알림을 생성하고 NotificationService를 호출한다")
    void createAlert_success() {

        // given
        Project project = mock(Project.class);
        Log log = mock(Log.class);
        Alert alert = mock(Alert.class);

        when(alertRepository.save(any(Alert.class)))
                .thenReturn(alert);

        // when
        alertService.createAlert(project, log);

        // then
        verify(alertRepository)
                .save(any(Alert.class));

        verify(notificationService)
                .sendAlert(alert);
    }

    @Test
    @DisplayName("알림 조회 - 프로젝트의 알림 목록을 정상적으로 조회한다")
    void getAlerts_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        Alert alert1 = mock(Alert.class);
        Alert alert2 = mock(Alert.class);

        Log log1 = mock(Log.class);
        Log log2 = mock(Log.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(alertRepository
                .findAllByProjectAndDeletedAtIsNullOrderByCreatedAtDesc(project))
                .thenReturn(List.of(alert1, alert2));

        // Alert → Log 연결
        when(alert1.getLog())
                .thenReturn(log1);

        when(alert2.getLog())
                .thenReturn(log2);

        // Log ID 설정
        when(log1.getId())
                .thenReturn(100L);

        when(log2.getId())
                .thenReturn(101L);

        // when
        List<AlertResponse> result =
                alertService.getAlerts(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .hasSize(2);

        verify(memberRepository)
                .findById(memberId);

        verify(projectRepository)
                .findByIdAndMemberAndStatus(
                        projectId,
                        member,
                        ProjectStatus.ACTIVE
                );

        verify(alertRepository)
                .findAllByProjectAndDeletedAtIsNullOrderByCreatedAtDesc(project);
    }


    @Test
    @DisplayName("알림 조회 - 알림이 없으면 빈 목록을 반환한다")
    void getAlerts_empty() {

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

        when(alertRepository
                .findAllByProjectAndDeletedAtIsNullOrderByCreatedAtDesc(project))
                .thenReturn(List.of());

        // when
        List<AlertResponse> result =
                alertService.getAlerts(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("알림 읽음 처리 - 정상적으로 알림을 읽음 상태로 변경한다")
    void readAlert_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        Alert alert = mock(Alert.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(alertRepository.findByIdAndProjectAndDeletedAtIsNull(
                alertId,
                project
        )).thenReturn(Optional.of(alert));

        // when
        alertService.readAlert(
                memberId,
                projectId,
                alertId
        );

        // then
        verify(alert)
                .read();

        verify(alertRepository)
                .findByIdAndProjectAndDeletedAtIsNull(
                        alertId,
                        project
                );
    }


    @Test
    @DisplayName("알림 읽음 처리 - 존재하지 않는 알림이면 예외가 발생한다")
    void readAlert_notFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(alertRepository.findByIdAndProjectAndDeletedAtIsNull(
                alertId,
                project
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                alertService.readAlert(
                        memberId,
                        projectId,
                        alertId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.ALERT_NOT_FOUND
                );
    }

    @Test
    @DisplayName("알림 삭제 - 정상적으로 soft delete를 수행한다")
    void deleteAlert_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        Alert alert = mock(Alert.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(alertRepository.findByIdAndProjectAndDeletedAtIsNull(
                alertId,
                project
        )).thenReturn(Optional.of(alert));

        // when
        alertService.deleteAlert(
                memberId,
                projectId,
                alertId
        );

        // then
        verify(alert)
                .delete();

        verify(alertRepository)
                .findByIdAndProjectAndDeletedAtIsNull(
                        alertId,
                        project
                );
    }


    @Test
    @DisplayName("알림 삭제 - 존재하지 않는 알림이면 예외가 발생한다")
    void deleteAlert_notFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;
        Long alertId = 100L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(alertRepository.findByIdAndProjectAndDeletedAtIsNull(
                alertId,
                project
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                alertService.deleteAlert(
                        memberId,
                        projectId,
                        alertId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.ALERT_NOT_FOUND
                );
    }


    @Test
    @DisplayName("알림 조회 - 존재하지 않는 회원이면 예외가 발생한다")
    void getAlerts_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                alertService.getAlerts(
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

        verify(alertRepository, never())
                .findAllByProjectAndDeletedAtIsNullOrderByCreatedAtDesc(
                        any(Project.class)
                );
    }


    @Test
    @DisplayName("알림 조회 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void getAlerts_projectNotFound() {

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
                alertService.getAlerts(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(alertRepository, never())
                .findAllByProjectAndDeletedAtIsNullOrderByCreatedAtDesc(
                        any(Project.class)
                );
    }
}