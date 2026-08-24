package com.jhy.devpulse.domain.dashboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.dashboard.dto.response.DailyLogStatistics;
import com.jhy.devpulse.domain.dashboard.dto.response.DashboardResponse;
import com.jhy.devpulse.domain.log.entity.LogLevel;
import com.jhy.devpulse.domain.log.repository.LogRepository;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

        private final MemberRepository memberRepository;
        private final ProjectRepository projectRepository;
        private final LogRepository logRepository;

        public DashboardResponse getDashboard(
                        Long memberId,
                        Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository
                                .findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                long totalLogs = logRepository.countByProject(project);

                long errorLogs = logRepository.countByProjectAndLevel(project, LogLevel.ERROR);

                long warnLogs = logRepository.countByProjectAndLevel(project, LogLevel.WARN);

                long infoLogs = logRepository.countByProjectAndLevel(project, LogLevel.INFO);

                LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

                long todayLogs = logRepository.countByProjectAndCreatedAtAfter(project, startOfToday);

                return DashboardResponse.builder()
                                .projectName(project.getName())
                                .totalLogs(totalLogs)
                                .errorLogs(errorLogs)
                                .warnLogs(warnLogs)
                                .infoLogs(infoLogs)
                                .todayLogs(todayLogs)
                                .build();
        }

        @Transactional(readOnly = true)
        public List<DailyLogStatistics> getWeeklyStatistics(Long memberId, Long projectId) {

                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                if (!project.getMember().getId().equals(memberId)) {
                        throw new CustomException(ErrorCode.ACCESS_DENIED);
                }

                LocalDateTime startDate = LocalDateTime.now()
                                .minusDays(6)
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0);

                List<Object[]> result = logRepository.findWeeklyLogStatistics(
                                project,
                                startDate);

                return result.stream()
                                .map(row -> DailyLogStatistics.builder()
                                                .date(((java.sql.Date) row[0]).toLocalDate())
                                                .count(((Number) row[1]).longValue())
                                                .build())
                                .toList();
        }
}