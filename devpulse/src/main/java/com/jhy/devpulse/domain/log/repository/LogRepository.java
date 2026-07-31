package com.jhy.devpulse.domain.log.repository;

import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.project.entity.Project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    Page<Log> findAllByProject(Project project, Pageable pageable);
}
