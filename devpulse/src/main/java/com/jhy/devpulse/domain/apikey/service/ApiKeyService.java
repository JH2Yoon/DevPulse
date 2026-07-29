package com.jhy.devpulse.domain.apikey.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.apikey.dto.response.ApiKeyResponse;
import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.apikey.entity.ApiKeyStatus;
import com.jhy.devpulse.domain.apikey.repository.ApiKeyRepository;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyService {
        private final ApiKeyRepository apiKeyRepository;
        private final ProjectRepository projectRepository;
        private final MemberRepository memberRepository;

        private String generateApiKey() {

                String apiKey;

                do {

                        apiKey = "dp_live_" +
                                        UUID.randomUUID()
                                                        .toString()
                                                        .replace("-", "");

                } while (apiKeyRepository.existsByApiKey(apiKey));

                return apiKey;
        }

        @Transactional
        public ApiKeyResponse createApiKey(Long memberId, Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository
                                .findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                apiKeyRepository.findByProjectAndStatus(project, ApiKeyStatus.ACTIVE)
                                .ifPresent(apiKey -> {
                                        throw new CustomException(ErrorCode.API_KEY_ALREADY_EXISTS);
                                });

                ApiKey apiKey = ApiKey.create(project, generateApiKey());

                apiKeyRepository.save(apiKey);

                return ApiKeyResponse.from(apiKey);
        }

        public ApiKeyResponse getApiKey(Long memberId, Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository.findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                ApiKey apiKey = apiKeyRepository.findByProjectAndStatus(project, ApiKeyStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.API_KEY_NOT_FOUND));

                return ApiKeyResponse.from(apiKey);
        }

        @Transactional
        public ApiKeyResponse regenerateApiKey(Long memberId, Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository.findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                ApiKey oldApiKey = apiKeyRepository.findByProjectAndStatus(project, ApiKeyStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.API_KEY_NOT_FOUND));

                oldApiKey.revoke();

                ApiKey newApiKey = ApiKey.create(project, generateApiKey());

                apiKeyRepository.save(newApiKey);

                return ApiKeyResponse.from(newApiKey);
        }

}
