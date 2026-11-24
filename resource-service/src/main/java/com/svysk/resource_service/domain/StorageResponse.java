package com.svysk.resource_service.domain;

import lombok.Data;

@Data
public class StorageResponse {
    private Long id;
    private StorageType storageType;
    private String bucket;
    private String path;
}
