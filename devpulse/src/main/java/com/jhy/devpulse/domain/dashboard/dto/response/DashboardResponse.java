package com.jhy.devpulse.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private String projectName;

    private long totalLogs;

    private long errorLogs;

    private long warnLogs;

    private long infoLogs;

    private long todayLogs;

}