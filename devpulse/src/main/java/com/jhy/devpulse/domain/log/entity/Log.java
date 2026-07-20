package com.jhy.devpulse.domain.log.entity;

import com.jhy.devpulse.common.entity.BaseEntity;
import com.jhy.devpulse.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "logs")
public class Log extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 2000)
    private String message;

    @Lob
    private String stackTrace;
}