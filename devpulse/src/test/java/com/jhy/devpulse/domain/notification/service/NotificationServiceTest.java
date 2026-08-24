package com.jhy.devpulse.domain.notification.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.log.entity.LogLevel;
import com.jhy.devpulse.domain.notification.client.DiscordWebhookClient;
import com.jhy.devpulse.domain.project.entity.Project;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private DiscordWebhookClient discordWebhookClient;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    @DisplayName("알림 전송 - Alert 정보를 이용해 Discord 알림을 정상적으로 전송한다")
    void sendAlert_success() {

        // given
        Alert alert = mock(Alert.class);
        Log log = mock(Log.class);
        Project project = mock(Project.class);

        when(alert.getLog())
                .thenReturn(log);

        when(alert.getProject())
                .thenReturn(project);

        when(project.getName())
                .thenReturn("DevPulse");

        when(log.getLevel())
                .thenReturn(LogLevel.ERROR);

        when(log.getServiceName())
                .thenReturn("auth-service");

        when(log.getMessage())
                .thenReturn("Login failed");

        // when
        notificationService.sendAlert(alert);

        // then
        verify(discordWebhookClient)
                .send(anyString());
    }


    @Test
    @DisplayName("알림 전송 - Discord 메시지에 Alert 정보가 포함된다")
    void sendAlert_messageContainsAlertInfo() {

        // given
        Alert alert = mock(Alert.class);
        Log log = mock(Log.class);
        Project project = mock(Project.class);

        when(alert.getLog())
                .thenReturn(log);

        when(alert.getProject())
                .thenReturn(project);

        when(project.getName())
                .thenReturn("DevPulse");

        when(log.getLevel())
                .thenReturn(LogLevel.ERROR);

        when(log.getServiceName())
                .thenReturn("auth-service");

        when(log.getMessage())
                .thenReturn("Login failed");

        // Discord로 전달되는 메시지 캡처
        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        // when
        notificationService.sendAlert(alert);

        // then
        verify(discordWebhookClient)
                .send(messageCaptor.capture());

        String message = messageCaptor.getValue();

        assert message.contains("DevPulse");
        assert message.contains("ERROR");
        assert message.contains("auth-service");
        assert message.contains("Login failed");
    }


    @Test
    @DisplayName("알림 전송 - Discord 전송 실패 시에도 예외를 발생시키지 않는다")
    void sendAlert_discordFailure() {

        // given
        Alert alert = mock(Alert.class);
        Log log = mock(Log.class);
        Project project = mock(Project.class);

        when(alert.getLog())
                .thenReturn(log);

        when(alert.getProject())
                .thenReturn(project);

        when(project.getName())
                .thenReturn("DevPulse");

        when(log.getLevel())
                .thenReturn(LogLevel.ERROR);

        when(log.getServiceName())
                .thenReturn("auth-service");

        when(log.getMessage())
                .thenReturn("Login failed");

        doThrow(new RuntimeException("Discord connection failed"))
                .when(discordWebhookClient)
                .send(anyString());

        // when
        notificationService.sendAlert(alert);

        // then
        verify(discordWebhookClient)
                .send(anyString());
    }
}