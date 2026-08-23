package com.jhy.devpulse.domain.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.jhy.devpulse.domain.auth.dto.request.SignInRequest;
import com.jhy.devpulse.domain.auth.dto.request.SignUpRequest;
import com.jhy.devpulse.domain.auth.dto.response.TokenResponse;
import com.jhy.devpulse.domain.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("회원가입 - 정상적으로 회원가입한다")
    void signUp_success() {

        // given
        SignUpRequest request = mock(SignUpRequest.class);

        doNothing()
                .when(authService)
                .signUp(request);

        // when
        ResponseEntity<Void> result =
                authController.signUp(request);

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(201);

        assertThat(result.getBody())
                .isNull();

        verify(authService)
                .signUp(request);
    }

    @Test
    @DisplayName("로그인 - 정상적으로 로그인하고 토큰을 반환한다")
    void signIn_success() {

        // given
        SignInRequest request = mock(SignInRequest.class);
        TokenResponse tokenResponse = mock(TokenResponse.class);

        when(authService.signIn(request))
                .thenReturn(tokenResponse);

        // when
        ResponseEntity<TokenResponse> result =
                authController.signIn(request);

        // then
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody())
                .isSameAs(tokenResponse);

        verify(authService)
                .signIn(request);
    }
}