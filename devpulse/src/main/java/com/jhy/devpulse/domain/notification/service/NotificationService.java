package com.jhy.devpulse.domain.notification.service;

import org.springframework.stereotype.Service;

import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.notification.client.DiscordWebhookClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final DiscordWebhookClient discordWebhookClient;

    public void sendAlert(Alert alert) {

        Log alertLog = alert.getLog();

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
                        alertLog.getLevel(),
                        alertLog.getServiceName(),
                        alertLog.getMessage(),
                        alert.getCreatedAt());

        try {
            discordWebhookClient.send(message);

        } catch (Exception e) {

            log.error(
                    "Discord notification failed. alertId={}",
                    alert.getId(),
                    e);
        }
    }
}