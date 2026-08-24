package com.jhy.devpulse.domain.log.dto.request;

import com.jhy.devpulse.domain.log.entity.LogLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogSearchCondition {
    private String keyword;

    private LogLevel level;

    private String serviceName;
}