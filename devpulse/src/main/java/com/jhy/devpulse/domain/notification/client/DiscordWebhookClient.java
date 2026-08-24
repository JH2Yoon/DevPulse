package com.jhy.devpulse.domain.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.jhy.devpulse.domain.notification.dto.request.DiscordWebhookRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DiscordWebhookClient {

    @Value("${discord.webhook-url}")
    private String webhookUrl;

    private final RestClient restClient;

    public void send(String message) {

        DiscordWebhookRequest request = DiscordWebhookRequest.builder()
                .content(message)
                .build();

        restClient.post()
                .uri(webhookUrl)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}