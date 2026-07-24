package com.jhy.devpulse.domain.project.repository;

import com.jhy.devpulse.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
