package com.jhy.devpulse.domain.apikey.repository;

import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.apikey.entity.ApiKeyStatus;
import com.jhy.devpulse.domain.project.entity.Project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
        Optional<ApiKey> findByProjectAndStatus(
                        Project project,
                        ApiKeyStatus status);

        Optional<ApiKey> findByApiKeyAndStatus(
                        String apiKey,
                        ApiKeyStatus status);

        boolean existsByApiKey(String apiKey);
}
