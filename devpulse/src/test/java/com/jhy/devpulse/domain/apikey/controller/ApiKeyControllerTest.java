package com.jhy.devpulse.domain.apikey.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.jhy.devpulse.domain.apikey.dto.response.ApiKeyResponse;
import com.jhy.devpulse.domain.apikey.service.ApiKeyService;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerTest {

    @Mock
    private ApiKeyService apiKeyService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApiKeyController apiKeyController;

    @Test
    @DisplayName("API Key 발급 - 정상적으로 API Key를 발급한다")
    void createApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        ApiKeyResponse response = mock(ApiKeyResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(apiKeyService.createApiKey(
                memberId,
                projectId
        )).thenReturn(response);

        // when
        ResponseEntity<ApiKeyResponse> result =
                apiKeyController.createApiKey(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(201);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(apiKeyService)
                .createApiKey(
                        memberId,
                        projectId
                );
    }

    @Test
    @DisplayName("API Key 조회 - 정상적으로 API Key를 조회한다")
    void getApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        ApiKeyResponse response = mock(ApiKeyResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(apiKeyService.getApiKey(
                memberId,
                projectId
        )).thenReturn(response);

        // when
        ResponseEntity<ApiKeyResponse> result =
                apiKeyController.getApiKey(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(apiKeyService)
                .getApiKey(
                        memberId,
                        projectId
                );
    }

    @Test
    @DisplayName("API Key 재발급 - 정상적으로 API Key를 재발급한다")
    void regenerateApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        ApiKeyResponse response = mock(ApiKeyResponse.class);

        when(authentication.getPrincipal())
                .thenReturn(memberId);

        when(apiKeyService.regenerateApiKey(
                memberId,
                projectId
        )).thenReturn(response);

        // when
        ResponseEntity<ApiKeyResponse> result =
                apiKeyController.regenerateApiKey(
                        authentication,
                        projectId
                );

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(response);

        verify(apiKeyService)
                .regenerateApiKey(
                        memberId,
                        projectId
                );
    }
}