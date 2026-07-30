package com.jhy.devpulse.domain.log.repository;

import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.project.entity.Project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByProjectOrderByCreatedAtDesc(Project project);
}
