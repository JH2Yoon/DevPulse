package com.jhy.devpulse.domain.alert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.alert.entity.AlertStatus;
import com.jhy.devpulse.domain.project.entity.Project;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByProjectOrderByCreatedAtDesc(Project project);

    Optional<Alert> findByIdAndProject(Long id, Project project);

    long countByProjectAndStatus(Project project, AlertStatus status);
}
