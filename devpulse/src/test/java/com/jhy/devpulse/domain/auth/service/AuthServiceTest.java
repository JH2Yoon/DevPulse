package com.jhy.devpulse.domain.auth.service;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.common.security.JwtProvider;
import com.jhy.devpulse.domain.auth.dto.request.SignInRequest;
import com.jhy.devpulse.domain.auth.dto.request.SignUpRequest;
import com.jhy.devpulse.domain.auth.dto.response.TokenResponse;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 - 정상적으로 회원을 생성한다")
    void signUp_success() {

        // given
        SignUpRequest request = mock(SignUpRequest.class);

        when(request.getEmail())
                .thenReturn("test@test.com");

        when(request.getPassword())
                .thenReturn("password123");

        when(request.getName())
                .thenReturn("테스트");

        when(memberRepository.existsByEmail("test@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        // when
        authService.signUp(request);

        // then
        verify(memberRepository)
                .existsByEmail("test@test.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(memberRepository)
                .save(any(Member.class));
    }


    @Test
    @DisplayName("회원가입 - 이미 존재하는 이메일이면 예외가 발생한다")
    void signUp_emailAlreadyExists() {

        // given
        SignUpRequest request = mock(SignUpRequest.class);

        when(request.getEmail())
                .thenReturn("test@test.com");

        when(memberRepository.existsByEmail("test@test.com"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() ->
                authService.signUp(request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.EMAIL_ALREADY_EXISTS
                );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(memberRepository, never())
                .save(any(Member.class));
    }


    @Test
    @DisplayName("회원가입 - 암호화된 비밀번호를 저장한다")
    void signUp_passwordIsEncoded() {

        // given
        SignUpRequest request = mock(SignUpRequest.class);

        when(request.getEmail())
                .thenReturn("test@test.com");

        when(request.getPassword())
                .thenReturn("password123");

        when(request.getName())
                .thenReturn("테스트");

        when(memberRepository.existsByEmail("test@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        // when
        authService.signUp(request);

        // then
        ArgumentCaptor<Member> memberCaptor =
                ArgumentCaptor.forClass(Member.class);

        verify(memberRepository)
                .save(memberCaptor.capture());

        Member savedMember =
                memberCaptor.getValue();

        assertThat(savedMember.getPassword())
                .isEqualTo("encodedPassword");

        assertThat(savedMember.getEmail())
                .isEqualTo("test@test.com");

        assertThat(savedMember.getName())
                .isEqualTo("테스트");
    }


    @Test
    @DisplayName("로그인 - 정상적으로 Access Token과 Refresh Token을 발급한다")
    void signIn_success() {

        // given
        SignInRequest request = mock(SignInRequest.class);

        Member member = mock(Member.class);

        when(request.getEmail())
                .thenReturn("test@test.com");

        when(request.getPassword())
                .thenReturn("password123");

        when(memberRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(member));

        when(member.getPassword())
                .thenReturn("encodedPassword");

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"
        )).thenReturn(true);

        when(member.getId())
                .thenReturn(1L);

        when(jwtProvider.createAccessToken(
                1L,
                member.getRole()
        )).thenReturn("access-token");

        when(jwtProvider.createRefreshToken(1L))
                .thenReturn("refresh-token");

        // when
        TokenResponse result =
                authService.signIn(request);

        // then
        assertThat(result)
                .isNotNull();

        assertThat(result.getAccessToken())
                .isEqualTo("access-token");

        assertThat(result.getRefreshToken())
                .isEqualTo("refresh-token");

        verify(memberRepository)
                .findByEmail("test@test.com");

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "encodedPassword"
                );

        verify(jwtProvider)
                .createAccessToken(
                        1L,
                        member.getRole()
                );

        verify(jwtProvider)
                .createRefreshToken(1L);
    }


    @Test
    @DisplayName("로그인 - 존재하지 않는 이메일이면 예외가 발생한다")
    void signIn_memberNotFound() {

        // given
        SignInRequest request = mock(SignInRequest.class);

        when(request.getEmail())
                .thenReturn("notfound@test.com");

        when(memberRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                authService.signIn(request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtProvider, never())
                .createAccessToken(anyLong(), any());

        verify(jwtProvider, never())
                .createRefreshToken(anyLong());
    }


    @Test
    @DisplayName("로그인 - 비밀번호가 일치하지 않으면 예외가 발생한다")
    void signIn_invalidPassword() {

        // given
        SignInRequest request = mock(SignInRequest.class);

        Member member = mock(Member.class);

        when(request.getEmail())
                .thenReturn("test@test.com");

        when(request.getPassword())
                .thenReturn("wrongPassword");

        when(memberRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(member));

        when(member.getPassword())
                .thenReturn("encodedPassword");

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                authService.signIn(request)
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.INVALID_PASSWORD
                );

        verify(jwtProvider, never())
                .createAccessToken(anyLong(), any());

        verify(jwtProvider, never())
                .createRefreshToken(anyLong());
    }
}