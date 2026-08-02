package com.jhy.devpulse.domain.alert.entity;

import java.time.LocalDateTime;

import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.project.entity.Project;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.UNREAD;

    private LocalDateTime createdAt;

    public static Alert create(
            Project project,
            Log log) {

        return Alert.builder()
                .project(project)
                .log(log)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void read() {
        this.status = AlertStatus.READ;
    }
}