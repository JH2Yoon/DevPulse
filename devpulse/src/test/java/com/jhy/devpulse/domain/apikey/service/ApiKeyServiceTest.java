package com.jhy.devpulse.domain.apikey.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    @DisplayName("API Key 생성 - 정상적으로 API Key를 생성한다")
    void createApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        ApiKey apiKey = mock(ApiKey.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.empty());

        when(apiKeyRepository.existsByApiKey(anyString()))
                .thenReturn(false);

        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenReturn(apiKey);

        // when
        ApiKeyResponse result =
                apiKeyService.createApiKey(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .isNotNull();

        verify(memberRepository)
                .findById(memberId);

        verify(projectRepository)
                .findByIdAndMemberAndStatus(
                        projectId,
                        member,
                        ProjectStatus.ACTIVE
                );

        verify(apiKeyRepository)
                .findByProjectAndStatus(
                        project,
                        ApiKeyStatus.ACTIVE
                );

        verify(apiKeyRepository)
                .save(any(ApiKey.class));
    }


    @Test
    @DisplayName("API Key 생성 - 존재하지 않는 회원이면 예외가 발생한다")
    void createApiKey_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.createApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));
    }


    @Test
    @DisplayName("API Key 생성 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void createApiKey_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.createApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));
    }


    @Test
    @DisplayName("API Key 생성 - 이미 활성화된 API Key가 있으면 예외가 발생한다")
    void createApiKey_alreadyExists() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        ApiKey existingApiKey = mock(ApiKey.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(existingApiKey));

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.createApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.API_KEY_ALREADY_EXISTS
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));

        verify(apiKeyRepository, never())
                .existsByApiKey(anyString());
    }


    @Test
    @DisplayName("API Key 조회 - 정상적으로 API Key를 조회한다")
    void getApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        ApiKey apiKey = mock(ApiKey.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(apiKey));

        // when
        ApiKeyResponse result =
                apiKeyService.getApiKey(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .isNotNull();

        verify(memberRepository)
                .findById(memberId);

        verify(projectRepository)
                .findByIdAndMemberAndStatus(
                        projectId,
                        member,
                        ProjectStatus.ACTIVE
                );

        verify(apiKeyRepository)
                .findByProjectAndStatus(
                        project,
                        ApiKeyStatus.ACTIVE
                );
    }


    @Test
    @DisplayName("API Key 조회 - 존재하지 않는 회원이면 예외가 발생한다")
    void getApiKey_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.getApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );
    }


    @Test
    @DisplayName("API Key 조회 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void getApiKey_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.getApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(apiKeyRepository, never())
                .findByProjectAndStatus(
                        any(Project.class),
                        any(ApiKeyStatus.class)
                );
    }


    @Test
    @DisplayName("API Key 조회 - 활성화된 API Key가 없으면 예외가 발생한다")
    void getApiKey_notFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.getApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.API_KEY_NOT_FOUND
                );
    }

    @Test
    @DisplayName("API Key 재발급 - 기존 API Key를 폐기하고 새로운 API Key를 생성한다")
    void regenerateApiKey_success() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);
        ApiKey oldApiKey = mock(ApiKey.class);
        ApiKey newApiKey = mock(ApiKey.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.of(oldApiKey));

        when(apiKeyRepository.existsByApiKey(anyString()))
                .thenReturn(false);

        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenReturn(newApiKey);

        // when
        ApiKeyResponse result =
                apiKeyService.regenerateApiKey(
                        memberId,
                        projectId
                );

        // then
        assertThat(result)
                .isNotNull();

        verify(oldApiKey)
                .revoke();

        verify(apiKeyRepository)
                .save(any(ApiKey.class));

        verify(apiKeyRepository)
                .existsByApiKey(anyString());
    }


    @Test
    @DisplayName("API Key 재발급 - 존재하지 않는 회원이면 예외가 발생한다")
    void regenerateApiKey_memberNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.regenerateApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.MEMBER_NOT_FOUND
                );

        verify(projectRepository, never())
                .findByIdAndMemberAndStatus(
                        anyLong(),
                        any(),
                        any()
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));
    }


    @Test
    @DisplayName("API Key 재발급 - 존재하지 않는 프로젝트이면 예외가 발생한다")
    void regenerateApiKey_projectNotFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.regenerateApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.PROJECT_NOT_FOUND
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));
    }


    @Test
    @DisplayName("API Key 재발급 - 기존 API Key가 없으면 예외가 발생한다")
    void regenerateApiKey_notFound() {

        // given
        Long memberId = 1L;
        Long projectId = 10L;

        Member member = mock(Member.class);
        Project project = mock(Project.class);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(projectRepository.findByIdAndMemberAndStatus(
                projectId,
                member,
                ProjectStatus.ACTIVE
        )).thenReturn(Optional.of(project));

        when(apiKeyRepository.findByProjectAndStatus(
                project,
                ApiKeyStatus.ACTIVE
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                apiKeyService.regenerateApiKey(
                        memberId,
                        projectId
                )
        )
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        ErrorCode.API_KEY_NOT_FOUND
                );

        verify(apiKeyRepository, never())
                .save(any(ApiKey.class));
    }
}