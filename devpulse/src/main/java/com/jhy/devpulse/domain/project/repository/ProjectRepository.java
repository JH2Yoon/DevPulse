package com.jhy.devpulse.domain.project.repository;

import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByMemberAndStatus(Member member, ProjectStatus status);

    Optional<Project> findByIdAndMemberAndStatus(Long id, Member member, ProjectStatus status);
}
