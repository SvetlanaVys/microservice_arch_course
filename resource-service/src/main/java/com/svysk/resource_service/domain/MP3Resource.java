package com.svysk.resource_service.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MP3Resource {

    private Long id;
    private String contentType;
    private String filePath;
    private Long size;
    private byte[] data;
    private LocalDateTime createdAt;
}
