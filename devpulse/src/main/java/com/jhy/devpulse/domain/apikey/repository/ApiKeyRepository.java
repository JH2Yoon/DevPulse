package com.jhy.devpulse.domain.apikey.repository;

import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByApiKey(String apiKey);

}
