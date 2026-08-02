package com.jhy.devpulse.domain.alert.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.alert.dto.response.AlertResponse;
import com.jhy.devpulse.domain.alert.entity.Alert;
import com.jhy.devpulse.domain.alert.repository.AlertRepository;
import com.jhy.devpulse.domain.log.entity.Log;
import com.jhy.devpulse.domain.member.entity.Member;
import com.jhy.devpulse.domain.member.repository.MemberRepository;
import com.jhy.devpulse.domain.project.entity.Project;
import com.jhy.devpulse.domain.project.entity.ProjectStatus;
import com.jhy.devpulse.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void createAlert(Project project, Log log) {

        Alert alert = Alert.create(project, log);

        alertRepository.save(alert);
    }

    public List<AlertResponse> getAlerts(
            Long memberId,
            Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository
                .findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        return alertRepository.findAllByProjectOrderByCreatedAtDesc(project)
                .stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Transactional
    public void readAlert(Long memberId, Long projectId, Long alertId) {

        Project project = getProject(memberId, projectId);

        Alert alert = alertRepository.findByIdAndProject(alertId, project)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_NOT_FOUND));

        alert.read();
    }

    @Transactional
    public void deleteAlert(Long memberId, Long projectId, Long alertId) {

        Project project = getProject(memberId, projectId);

        Alert alert = alertRepository.findByIdAndProject(alertId, project)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_NOT_FOUND));

        alertRepository.delete(alert);
    }

    private Project getProject(Long memberId, Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return projectRepository
                .findByIdAndMemberAndStatus(projectId, member, ProjectStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }
}