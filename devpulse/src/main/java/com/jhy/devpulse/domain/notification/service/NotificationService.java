package com.jhy.devpulse.domain.notification.service;

import org.springframework.stereotype.Service;

import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.notification.client.DiscordWebhookClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DiscordWebhookClient discordWebhookClient;

    public void sendAlert(Alert alert) {

        Log log = alert.getLog();

        String message = """
                🚨 **DevPulse Alert**

                **Project** : %s
                **Level** : %s
                **Service** : %s
                **Message** : %s
                **Time** : %s
                """
                .formatted(
                        alert.getProject().getName(),
                        log.getLevel(),
                        log.getServiceName(),
                        log.getMessage(),
                        alert.getCreatedAt());

        discordWebhookClient.send(message);
    }
}