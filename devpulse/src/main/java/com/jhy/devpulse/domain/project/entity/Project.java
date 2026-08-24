package com.jhy.devpulse.domain.project.entity;

import java.util.ArrayList;
import java.util.List;

import com.jhy.devpulse.common.entity.BaseEntity;
import com.jhy.devpulse.domain.apikey.entity.ApiKey;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "projects")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<ApiKey> apiKeys = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project")
    private List<Log> logs = new ArrayList<>();

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void deactivate() {
        this.status = ProjectStatus.INACTIVE;
        super.delete();
    }

    public static Project create(
            Member member,
            String name,
            String description) {
        return Project.builder()
                .member(member)
                .name(name)
                .description(description)
                .status(ProjectStatus.ACTIVE)
                .build();
    }
}