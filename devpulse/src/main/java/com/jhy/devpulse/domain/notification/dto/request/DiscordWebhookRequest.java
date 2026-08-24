package com.jhy.devpulse.domain.notification.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class DiscordWebhookRequest {

    private String content;

}