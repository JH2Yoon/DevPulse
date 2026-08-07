package com.jhy.devpulse.domain.log.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jhy.devpulse.domain.log.dto.request.LogSearchCondition;
import com.jhy.devpulse.domain.log.entity.LogDocument;

public interface LogSearchRepository {
    Page<LogDocument> search(
            Long projectId,
            LogSearchCondition condition,
            Pageable pageable);
}
