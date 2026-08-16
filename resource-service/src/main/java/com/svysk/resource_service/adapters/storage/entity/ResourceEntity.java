package com.svysk.resource_service.adapters.storage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "resource_files")
@Data
@ToString
@RequiredArgsConstructor
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(columnDefinition = "BYTEA", nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

