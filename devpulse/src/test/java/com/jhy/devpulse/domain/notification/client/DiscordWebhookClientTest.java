package com.jhy.devpulse.domain.notification.client;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.jhy.devpulse.domain.notification.dto.request.DiscordWebhookRequest;

@ExtendWith(MockitoExtension.class)
class DiscordWebhookClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private DiscordWebhookClient discordWebhookClient;


    @Test
    @DisplayName("Discord Webhook 전송 - 메시지를 정상적으로 전송한다")
    void send_success() {

        // given
        String webhookUrl = "https://discord.com/api/webhooks/test";
        String message = "🚨 DevPulse Alert";

        ReflectionTestUtils.setField(
                discordWebhookClient,
                "webhookUrl",
                webhookUrl
        );

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(webhookUrl))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(any(DiscordWebhookRequest.class)))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity())
                .thenReturn(null);

        // when
        discordWebhookClient.send(message);

        // then
        verify(restClient)
                .post();

        verify(requestBodyUriSpec)
                .uri(webhookUrl);

        verify(requestBodySpec)
                .body(any(DiscordWebhookRequest.class));

        verify(requestBodySpec)
                .retrieve();

        verify(responseSpec)
                .toBodilessEntity();
    }
}