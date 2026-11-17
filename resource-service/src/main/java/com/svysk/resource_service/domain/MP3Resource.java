package com.svysk.resource_service.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MP3Resource {

    private Long id;
    private String contentType;
    private Long size;
    private String bucketName;
    private String filePath;
    private byte[] data;
    private LocalDateTime createdAt;
    private StorageType status;
}
