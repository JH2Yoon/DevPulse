package com.jhy.devpulse.domain.log.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.alert.service.AlertService;
import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.apikey.entity.ApiKeyStatus;
import com.jhy.devpulse.domain.apikey.repository.ApiKeyRepository;
import com.jhy.devpulse.domain.log.dto.request.CreateLogRequest;
import com.jhy.devpulse.domain.log.dto.request.LogSearchCondition;
import com.jhy.devpulse.domain.log.dto.response.LogResponse;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.log.entity.LogDocument;
import com.jhy.devpulse.domain.log.entity.LogLevel;
import com.jhy.devpulse.domain.log.repository.LogDocumentRepository;
import com.jhy.devpulse.domain.log.repository.LogRepository;
import com.jhy.devpulse.domain.log.repository.LogSearchRepository;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {

        private final LogRepository logRepository;
        private final ApiKeyRepository apiKeyRepository;
        private final ProjectRepository projectRepository;
        private final MemberRepository memberRepository;
        private final LogDocumentRepository logDocumentRepository;
        private final LogSearchRepository logSearchRepository;

        private final AlertService alertService;

        @Transactional
        public void createLog(String apiKey, CreateLogRequest request) {

                ApiKey key = apiKeyRepository.findByApiKeyAndStatus(apiKey, ApiKeyStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.API_KEY_NOT_FOUND));

                Project project = projectRepository.findByIdAndStatus(key.getProject().getId(), ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                Log log = Log.create(
                                project,
                                request.getLevel(),
                                request.getServiceName(),
                                request.getMessage(),
                                request.getStackTrace());

                Log savedLog = logRepository.save(log);

                logDocumentRepository.save(
                                LogDocument.from(savedLog));

                if (savedLog.getLevel() == LogLevel.ERROR) {
                        alertService.createAlert(project, savedLog);
                }
        }

        public Page<LogResponse> getLogs(
                        Long memberId,
                        Long projectId,
                        LogSearchCondition condition,
                        Pageable pageable) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository
                                .findByIdAndMemberAndStatus(
                                                projectId,
                                                member,
                                                ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                return logSearchRepository
                                .search(project.getId(), condition, pageable)
                                .map(LogResponse::from);
        }

        public LogResponse getLog(Long memberId, Long logId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Log log = logRepository.findById(logId)
                                .orElseThrow(() -> new CustomException(ErrorCode.LOG_NOT_FOUND));

                if (!log.getProject().getMember().equals(member)) {
                        throw new CustomException(ErrorCode.ACCESS_DENIED);
                }

                return LogResponse.from(log);
        }
}