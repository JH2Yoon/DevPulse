package com.jhy.devpulse.domain.apikey.dto.response;

import com.jhy.devpulse.domain.apikey.entity.ApiKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiKeyResponse {

    private String apiKey;

    public static ApiKeyResponse from(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .apiKey(apiKey.getApiKey())
                .build();
    }
}