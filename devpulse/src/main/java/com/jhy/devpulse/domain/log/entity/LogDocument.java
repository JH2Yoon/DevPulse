package com.jhy.devpulse.domain.log.entity;

import java.time.LocalDateTime;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import org.springframework.data.annotation.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "logs")
public class LogDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long projectId;

    @Field(type = FieldType.Keyword)
    private LogLevel level;

    @Field(type = FieldType.Keyword)
    private String serviceName;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Text)
    private String stackTrace;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    public static LogDocument from(Log log) {
        return LogDocument.builder()
                .id(log.getId())
                .projectId(log.getProject().getId())
                .level(log.getLevel())
                .serviceName(log.getServiceName())
                .message(log.getMessage())
                .stackTrace(log.getStackTrace())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
