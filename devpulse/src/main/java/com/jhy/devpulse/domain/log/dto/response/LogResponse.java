package com.jhy.devpulse.domain.log.dto.response;

import java.time.LocalDateTime;

import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.log.entity.LogLevel;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LogResponse {

    private Long id;
    private LogLevel level;
    private String serviceName;
    private String message;
    private String stackTrace;
    private LocalDateTime createdAt;

    public static LogResponse from(Log log) {

        return LogResponse.builder()
                .id(log.getId())
                .level(log.getLevel())
                .serviceName(log.getServiceName())
                .message(log.getMessage())
                .stackTrace(log.getStackTrace())
                .createdAt(log.getCreatedAt())
                .build();
    }
}