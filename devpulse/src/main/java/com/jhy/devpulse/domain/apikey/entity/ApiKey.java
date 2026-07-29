package com.jhy.devpulse.domain.apikey.entity;

import java.time.LocalDateTime;

import com.jhy.devpulse.common.entity.BaseEntity;
import com.jhy.devpulse.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "api_keys")
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, unique = true, length = 255)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApiKeyStatus status = ApiKeyStatus.ACTIVE;

    private LocalDateTime expiredAt;

    public void regenerate(String apiKey) {
        this.apiKey = apiKey;
        this.status = ApiKeyStatus.ACTIVE;
        this.expiredAt = null;
    }

    public void revoke() {
        this.status = ApiKeyStatus.INACTIVE;
        this.expiredAt = LocalDateTime.now();
    }

    public static ApiKey create(Project project, String apiKey) {
        return ApiKey.builder()
                .project(project)
                .apiKey(apiKey)
                .status(ApiKeyStatus.ACTIVE)
                .build();
    }
}
