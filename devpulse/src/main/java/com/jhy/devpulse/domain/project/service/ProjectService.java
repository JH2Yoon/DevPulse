package com.jhy.devpulse.domain.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.dto.request.CreateProjectRequest;
import com.jhy.devpulse.domain.project.dto.request.UpdateProjectRequest;
import com.jhy.devpulse.domain.project.dto.response.ProjectResponse;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

        private final ProjectRepository projectRepository;
        private final MemberRepository memberRepository;

        @Transactional
        public void createProject(Long memberId, CreateProjectRequest request) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = Project.create(
                                member,
                                request.getName(),
                                request.getDescription());

                projectRepository.save(project);
        }

        public List<ProjectResponse> getProjects(Long memberId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                return projectRepository.findAllByMemberAndStatus(
                                member,
                                ProjectStatus.ACTIVE)
                                .stream()
                                .map(ProjectResponse::from)
                                .toList();
        }

        public ProjectResponse getProject(Long memberId, Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository.findByIdAndMemberAndStatus(
                                projectId,
                                member,
                                ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                return ProjectResponse.from(project);
        }

        @Transactional
        public void updateProject(
                        Long memberId,
                        Long projectId,
                        UpdateProjectRequest request) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository.findByIdAndMemberAndStatus(
                                projectId,
                                member,
                                ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                project.update(
                                request.getName(),
                                request.getDescription());
        }

        @Transactional
        public void deleteProject(Long memberId, Long projectId) {

                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

                Project project = projectRepository.findByIdAndMemberAndStatus(
                                projectId,
                                member,
                                ProjectStatus.ACTIVE)
                                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

                project.deactivate();
        }
}