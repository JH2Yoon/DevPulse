package com.jhy.devpulse.domain.log.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.jhy.devpulse.domain.log.entity.LogDocument;

public interface LogDocumentRepository extends ElasticsearchRepository<LogDocument, Long> {
}