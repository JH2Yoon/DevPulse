package com.jhy.devpulse.domain.alert.dto.response;

import java.time.LocalDateTime;

import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.alert.entity.AlertStatus;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.log.entity.LogLevel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertResponse {

    private Long id;

    private Long logId;

    private LogLevel level;

    private String serviceName;

    private String message;

    private AlertStatus status;

    private LocalDateTime createdAt;

    public static AlertResponse from(Alert alert) {

        Log log = alert.getLog();

        return AlertResponse.builder()
                .id(alert.getId())
                .logId(log.getId())
                .level(log.getLevel())
                .serviceName(log.getServiceName())
                .message(log.getMessage())
                .status(alert.getStatus())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
