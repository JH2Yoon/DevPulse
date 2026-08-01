package com.jhy.devpulse.domain.log.repository;

import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.log.entity.LogLevel;
import com.jhy.devpulse.domain.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<Log, Long> {
    Page<Log> findAllByProject(Project project, Pageable pageable);

    long countByProject(Project project);

    long countByProjectAndLevel(Project project, LogLevel level);

    long countByProjectAndCreatedAtAfter(Project project, LocalDateTime createdAt);

    @Query("""
                SELECT
                    FUNCTION('DATE', l.createdAt) AS date,
                    COUNT(l) AS count
                FROM Log l
                WHERE l.project = :project
                AND l.createdAt >= :startDate
                GROUP BY FUNCTION('DATE', l.createdAt)
                ORDER BY FUNCTION('DATE', l.createdAt)
            """)
    List<Object[]> findWeeklyLogStatistics(@Param("project") Project project,
            @Param("startDate") LocalDateTime startDate);
}
