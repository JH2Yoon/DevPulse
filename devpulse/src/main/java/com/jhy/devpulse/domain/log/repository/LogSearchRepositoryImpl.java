package com.jhy.devpulse.domain.log.repository;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.stereotype.Repository;

import com.jhy.devpulse.domain.log.entity.LogDocument;
import com.jhy.devpulse.domain.log.dto.request.LogSearchCondition;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LogSearchRepositoryImpl implements LogSearchRepository {

        private final ElasticsearchOperations elasticsearchOperations;

        @Override
        public Page<LogDocument> search(
                        Long projectId,
                        LogSearchCondition condition,
                        Pageable pageable) {

                Query query = Query.of(q -> q.bool(bool -> {

                        // 프로젝트 조건
                        bool.must(must -> must
                                        .term(term -> term
                                                        .field("projectId")
                                                        .value(projectId)));

                        // 키워드 검색
                        if (condition.getKeyword() != null
                                        && !condition.getKeyword().isBlank()) {

                                bool.must(must -> must
                                                .match(match -> match
                                                                .field("message")
                                                                .query(condition.getKeyword())));
                        }

                        // 로그 레벨
                        if (condition.getLevel() != null) {

                                bool.must(must -> must
                                                .term(term -> term
                                                                .field("level")
                                                                .value(condition.getLevel().name())));
                        }

                        // 서비스 이름
                        if (condition.getServiceName() != null
                                        && !condition.getServiceName().isBlank()) {

                                bool.must(must -> must
                                                .term(term -> term
                                                                .field("serviceName")
                                                                .value(condition.getServiceName())));
                        }

                        return bool;
                }));

                NativeQuery searchQuery = NativeQuery.builder()
                                .withQuery(query)
                                .withPageable(pageable)
                                .build();

                SearchHits<LogDocument> searchHits = elasticsearchOperations.search(
                                searchQuery,
                                LogDocument.class);

                List<LogDocument> logs = searchHits.getSearchHits()
                                .stream()
                                .map(SearchHit::getContent)
                                .toList();

                return new PageImpl<>(
                                logs,
                                pageable,
                                searchHits.getTotalHits());
        }
}