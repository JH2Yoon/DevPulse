package com.jhy.devpulse.domain.log.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.apikey.entity.ApiKeyStatus;
import com.jhy.devpulse.domain.apikey.repository.ApiKeyRepository;
import com.jhy.devpulse.domain.log.dto.request.CreateLogRequest;
import com.jhy.devpulse.domain.log.dto.response.LogResponse;
import com.jhy.devpulse.domain.log.entity.Log;
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
public class LogService {

    private final LogRepository logRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

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

        logRepository.save(log);
    }

    public List<LogResponse> getLogs(
            Long memberId,
            Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        return logRepository
                .findAllByProjectOrderByCreatedAtDesc(project)
                .stream()
                .map(LogResponse::from)
                .toList();
    }
}