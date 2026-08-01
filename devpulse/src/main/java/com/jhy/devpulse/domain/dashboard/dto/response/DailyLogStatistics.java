package com.jhy.devpulse.domain.dashboard.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyLogStatistics {

    private LocalDate date;

    private Long count;

}