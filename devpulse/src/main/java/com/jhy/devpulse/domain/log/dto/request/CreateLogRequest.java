package com.jhy.devpulse.domain.log.dto.request;

import com.jhy.devpulse.domain.log.entity.LogLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLogRequest {

    @NotNull
    private LogLevel level;

    @NotBlank
    @Size(max = 100)
    private String serviceName;

    @NotBlank
    @Size(max = 2000)
    private String message;

    private String stackTrace;
}